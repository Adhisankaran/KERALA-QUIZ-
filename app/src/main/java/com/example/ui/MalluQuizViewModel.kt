package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class QuizScreen {
    DASHBOARD,
    QUIZ_PREPARE,
    QUIZ_RUNNING,
    QUIZ_RESULTS,
    QUIZ_REVIEW,
    HISTORY_REVIEW
}

data class ActiveQuizState(
    val dayId: Int = 0,
    val dayName: String = "",
    val dayDate: String = "",
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val timerMaxSeconds: Int = 30, // 0 for unlimited
    val timerRemainingSeconds: Int = 30,
    val isTimerActive: Boolean = false,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val isFetching: Boolean = false,
    val fetchError: String? = null,
    val finished: Boolean = false
)

class MalluQuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = QuizRepository(database.quizHistoryDao())
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow(QuizScreen.DASHBOARD)
    val currentScreen: StateFlow<QuizScreen> = _currentScreen.asStateFlow()

    // Database History
    val quizHistoryList: StateFlow<List<QuizHistory>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Calendar State
    private val _selectedYear = MutableStateFlow(2026) // Centered on current context year
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(6) // June (extracted from current local time June 2026)
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    // Active Selected Day for Detail View or Preparing Quiz
    private val _selectedDay = MutableStateFlow<CelebratedDay?>(null)
    val selectedDay: StateFlow<CelebratedDay?> = _selectedDay.asStateFlow()

    // Active Quiz Play State
    private val _activeQuizState = MutableStateFlow(ActiveQuizState())
    val activeQuizState: StateFlow<ActiveQuizState> = _activeQuizState.asStateFlow()

    // History Review Target state
    private val _reviewingHistory = MutableStateFlow<QuizHistory?>(null)
    val reviewingHistory: StateFlow<QuizHistory?> = _reviewingHistory.asStateFlow()

    // Timer Job
    private var timerJob: Job? = null

    // Filtered Days list helper
    val filteredDays: StateFlow<List<CelebratedDay>> = _searchQuery
        .combine(_selectedMonth) { query, month ->
            if (query.isBlank()) {
                // If query is blank, we can also display matching days of the selected month
                DayData.celebratedDays.filter { it.month == month }
            } else {
                DayData.searchDays(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectMonth(month: Int) {
        _selectedMonth.value = month
        // Reset query when switching month to clear search confusion unless typing
    }

    fun selectDay(day: CelebratedDay?) {
        _selectedDay.value = day
    }

    fun navigateTo(screen: QuizScreen) {
        _currentScreen.value = screen
        if (screen == QuizScreen.DASHBOARD) {
            cancelTimer()
        }
    }

    fun changeYear(offset: Int) {
        _selectedYear.value = (_selectedYear.value + offset).coerceIn(2020, 2030)
    }

    fun startPreparingQuiz(day: CelebratedDay) {
        _selectedDay.value = day
        _currentScreen.value = QuizScreen.QUIZ_PREPARE
    }

    fun startQuiz(count: Int, timerSeconds: Int) {
        val day = _selectedDay.value ?: return
        
        _activeQuizState.value = ActiveQuizState(
            dayId = day.id,
            dayName = day.name,
            dayDate = "${getMonthNameInMalayalam(day.month)} ${day.day}",
            totalQuestions = count,
            timerMaxSeconds = timerSeconds,
            isFetching = true,
            fetchError = null
        )
        
        _currentScreen.value = QuizScreen.QUIZ_RUNNING

        viewModelScope.launch {
            try {
                val fetchedQuestions = repository.generateQuiz(
                    dayId = day.id,
                    dayName = day.name,
                    dayDateString = "${day.day} ${englishMonthName(day.month)}",
                    questionCount = count
                )
                
                _activeQuizState.value = _activeQuizState.value.copy(
                    questions = fetchedQuestions,
                    isFetching = false,
                    totalQuestions = fetchedQuestions.size
                )
                
                startQuestionTimer()
            } catch (e: Exception) {
                Log.e("MalluQuizViewModel", "Failed to load questions", e)
                _activeQuizState.value = _activeQuizState.value.copy(
                    isFetching = false,
                    fetchError = "ചോദ്യങ്ങൾ വീണ്ടെടുക്കുന്നതിൽ പരാജയപ്പെട്ടു. ദയവായി വീണ്ടും ശ്രമിക്കുക."
                )
            }
        }
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        val currentState = _activeQuizState.value
        if (currentState.timerMaxSeconds <= 0) return // Unlimited timer

        _activeQuizState.value = currentState.copy(
            timerRemainingSeconds = currentState.timerMaxSeconds,
            isTimerActive = true
        )

        timerJob = viewModelScope.launch {
            while (_activeQuizState.value.timerRemainingSeconds > 0 && _activeQuizState.value.isTimerActive) {
                delay(1000)
                val remaining = _activeQuizState.value.timerRemainingSeconds
                if (remaining <= 1) {
                    // Timeout! Auto-advance as unanswered (-1)
                    handleQuestionTimeout()
                } else {
                    _activeQuizState.value = _activeQuizState.value.copy(
                        timerRemainingSeconds = remaining - 1
                    )
                }
            }
        }
    }

    private fun handleQuestionTimeout() {
        selectAnswer(-1) // Submit -1 to auto-advance
    }

    fun selectAnswer(optionIndex: Int) {
        timerJob?.cancel()
        val curState = _activeQuizState.value
        val questions = curState.questions
        val curIndex = curState.currentQuestionIndex

        if (curIndex >= questions.size) return

        // Save selected index to the question
        questions[curIndex].selectedIndex = optionIndex

        // Calculate if it's correct
        val isCorrect = optionIndex == questions[curIndex].answerIndex
        val newScore = if (isCorrect) curState.score + 1 else curState.score

        val nextIndex = curIndex + 1
        if (nextIndex < questions.size) {
            _activeQuizState.value = curState.copy(
                currentQuestionIndex = nextIndex,
                score = newScore
            )
            startQuestionTimer()
        } else {
            // Finished!
            _activeQuizState.value = curState.copy(
                score = newScore,
                finished = true
            )
            saveQuizResultToDatabase()
            _currentScreen.value = QuizScreen.QUIZ_RESULTS
        }
    }

    private fun saveQuizResultToDatabase() {
        val state = _activeQuizState.value
        val jsonQuestions = QuizRepository.questionsToJson(state.questions)
        val historyRecord = QuizHistory(
            dayName = state.dayName,
            dayDate = state.dayDate,
            score = state.score,
            totalQuestions = state.totalQuestions,
            timestamp = System.currentTimeMillis(),
            questionsJson = jsonQuestions
        )
        viewModelScope.launch {
            repository.insertQuiz(historyRecord)
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _activeQuizState.value = _activeQuizState.value.copy(isTimerActive = false)
    }

    fun startReview() {
        _currentScreen.value = QuizScreen.QUIZ_REVIEW
    }

    fun showHistoryReview(history: QuizHistory) {
        _reviewingHistory.value = history
        _currentScreen.value = QuizScreen.HISTORY_REVIEW
    }

    fun deleteHistoryRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteQuizById(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // Helper helper translation months
    fun getMonthNameInMalayalam(month: Int): String {
        return when (month) {
            1 -> "ജനുവരി"
            2 -> "ഫെബ്രുവരി"
            3 -> "മാർച്ച്"
            4 -> "ഏപ്രിൽ"
            5 -> "മേയ്"
            6 -> "ജൂൺ"
            7 -> "ജൂലൈ"
            8 -> "ഓഗസ്റ്റ്"
            9 -> "സെപ്റ്റംബർ"
            10 -> "ഒക്ടോബർ"
            11 -> "നവംബർ"
            12 -> "ഡിസംബർ"
            else -> ""
        }
    }

    private fun englishMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
