package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val viewModel: MalluQuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Elegant Traditional Kerala Theme Definition
            val primaryKerala = Color(0xFF0F5A37)     // Deep Kerala Forest Green
            val secondaryKerala = Color(0xFFC7A02C)   // Traditional Kasavu Gold
            val tertiaryKerala = Color(0xFFD35400)    // Saffron Spice Orange
            val bgKerala = Color(0xFFF9FBF8)          // Ivory Milk White
            val surfaceKerala = Color(0xFFFFFFFF)

            val colors = lightColorScheme(
                primary = primaryKerala,
                onPrimary = Color.White,
                secondary = secondaryKerala,
                onSecondary = Color(0xFF1D2912),
                tertiary = tertiaryKerala,
                background = bgKerala,
                surface = surfaceKerala,
                onBackground = Color(0xFF1D1F1C),
                onSurface = Color(0xFF1D1F1C)
            )

            MaterialTheme(colorScheme = colors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                    
                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(300),
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            QuizScreen.DASHBOARD -> DashboardScreen(viewModel)
                            QuizScreen.QUIZ_PREPARE -> QuizPrepareScreen(viewModel)
                            QuizScreen.QUIZ_RUNNING -> QuizRunningScreen(viewModel)
                            QuizScreen.QUIZ_RESULTS -> QuizResultsScreen(viewModel)
                            QuizScreen.QUIZ_REVIEW -> QuizReviewScreen(viewModel, isPastHistory = false)
                            QuizScreen.HISTORY_REVIEW -> QuizReviewScreen(viewModel, isPastHistory = true)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MalluQuizViewModel) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()
    val filteredDays by viewModel.filteredDays.collectAsStateWithLifecycle()
    val historyList by viewModel.quizHistoryList.collectAsStateWithLifecycle()

    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MALLU QUIZ",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Traditional Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        Color(0xFF073821)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = "കേരള കായ്കനി വിവരങ്ങളും സ്മാരക ദിവസങ്ങളും!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "കേരളത്തിലെയും അന്താരാഷ്ട്രതലത്തിലെയും പ്രാധാന്യമുള്ള ദിവസങ്ങളെയും സ്മരണകളെയും കണ്ടെത്തൂ, മനോഹരമായ മലയാളം ക്വിസ്സുകളിലൂടെ പഠിക്കൂ.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Search Bar Component
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_day_input"),
                    placeholder = {
                        Text(
                            "ദിവസങ്ങൾ തിരയുക (ഉദാ: കേരളപ്പിറവി, ഓണം...)",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    ),
                    singleLine = true
                )
            }

            // Month Swiper Bar (Only active when searching query is blank)
            if (searchQuery.isBlank()) {
                item {
                    Column {
                        Text(
                            text = "മാസം തിരഞ്ഞെടുക്കുക",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items((1..12).toList()) { m ->
                                val mName = viewModel.getMonthNameInMalayalam(m)
                                val isSelected = selectedMonth == m
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                        )
                                        .clickable { viewModel.selectMonth(m) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = mName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Interactive Calendar Grid representation
                item {
                    val daysInMonth = getDaysInMonth(selectedMonth, selectedYear)
                    val startDayOfWeek = getStartDayOfWeek(selectedMonth, selectedYear)
                    val leadingEmptyCells = startDayOfWeek - 1 // Sunday as index 1

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Calendar Header (Month + Year Switcher)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${viewModel.getMonthNameInMalayalam(selectedMonth)} $selectedYear",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row {
                                    IconButton(onClick = { viewModel.changeYear(-1) }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Previous Year",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { viewModel.changeYear(1) }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Next Year",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Seven Days week names row in Malayalam abbreviations
                            val weekDays = listOf("ഞാ", "തി", "ചൊ", "ബു", "വ്യാ", "വെ", "ശ")
                            Row(modifier = Modifier.fillMaxWidth()) {
                                weekDays.forEach { dayOfWeek ->
                                    Text(
                                        text = dayOfWeek,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (dayOfWeek == "ഞാ") MaterialTheme.colorScheme.tertiary else Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Grid of Dates
                            val totalCells = leadingEmptyCells + daysInMonth
                            val rowCount = (totalCells + 6) / 7

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                for (r in 0 until rowCount) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        for (c in 0..6) {
                                            val cellIdx = r * 7 + c
                                            val dayNum = cellIdx - leadingEmptyCells + 1
                                            
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (dayNum in 1..daysInMonth) {
                                                    // Check if this date is a celebrated day
                                                    val matchDay = DayData.celebratedDays.firstOrNull {
                                                        it.month == selectedMonth && it.day == dayNum
                                                    }
                                                    val isSelected = selectedDay?.month == selectedMonth && selectedDay?.day == dayNum

                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                when {
                                                                    isSelected -> MaterialTheme.colorScheme.secondary
                                                                    matchDay != null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                                    else -> Color.Transparent
                                                                }
                                                            )
                                                            .clickable {
                                                                if (matchDay != null) {
                                                                    viewModel.selectDay(matchDay)
                                                                } else {
                                                                    // Show general Custom day details click
                                                                    viewModel.selectDay(
                                                                        CelebratedDay(
                                                                            id = -1,
                                                                            name = "${viewModel.getMonthNameInMalayalam(selectedMonth)} $dayNum",
                                                                            englishName = "${dayNum}th Day",
                                                                            day = dayNum,
                                                                            month = selectedMonth,
                                                                            category = "GENERAL KNOWLEDGE",
                                                                            description = "കേരള സംസ്കാരത്തെയും ചരിത്രത്തെയും കുറിച്ചുള്ള പൊതുപഠനം.",
                                                                            celebrationDetails = "$dayNum-ാം തീയതിയിൽ പ്രത്യേക ദിനങ്ങൾ ഒന്നും മാപ്പ് ചെയ്തിട്ടില്ലെങ്കിലും, ഈ തീയതി മുന്നിർത്തി കേരള ചരിത്രം, സംസ്കാരം, ഭൂമിശാസ്ത്രം എന്നിവയിലുള്ള ചോദ്യങ്ങൾ അടങ്ങിയ പൊതുവിജ്ഞാന ക്വിസ് പരിശീലിക്കാം."
                                                                        )
                                                                    )
                                                                }
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                            Text(
                                                                text = dayNum.toString(),
                                                                fontSize = 11.sp,
                                                                fontWeight = if (matchDay != null || isSelected) FontWeight.Bold else FontWeight.Normal,
                                                                color = when {
                                                                    isSelected -> Color.Black
                                                                    matchDay != null -> MaterialTheme.colorScheme.primary
                                                                    else -> MaterialTheme.colorScheme.onBackground
                                                                }
                                                            )
                                                            if (matchDay != null && !isSelected) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(4.dp)
                                                                        .clip(CircleShape)
                                                                        .background(MaterialTheme.colorScheme.secondary)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Current Chosen / Clicked Day Details Card Component
            selectedDay?.let { day ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${viewModel.getMonthNameInMalayalam(day.month)} ${day.day}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = day.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = day.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = day.description,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = day.celebrationDetails,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Start Learning and Quiz button triggers
                            Button(
                                onClick = { viewModel.startPreparingQuiz(day) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_quiz_trigger"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "ക്വിസ് പരിശീലനം ആരംഭിക്കുക",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Important Days results list (for simple browsing or when searching)
            item {
                Text(
                    text = if (searchQuery.isNotBlank()) "തിരഞ്ഞെടുത്ത ദിവസങ്ങൾ (${filteredDays.size})" else "ഈ മാസത്തിലെ പ്രധാന ദിവസങ്ങൾ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (filteredDays.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "അനുയോജ്യമായ ദിവസങ്ങൾ ഒന്നും കണ്ടെത്തിയിട്ടില്ല.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(filteredDays) { day ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectDay(day) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = day.day.toString(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = viewModel.getMonthNameInMalayalam(day.month).take(3),
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = day.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = day.description,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Quiz History Component (ക്വിസ് ചരിത്രം)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ക്വിസ് സ്കോർ ചരിത്രം",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (historyList.isNotEmpty()) {
                        TextButton(onClick = { showClearConfirm = true }) {
                            Text(
                                "എല്ലാം മായ്‌ക്കുക",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            if (historyList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier.size(45.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "മുൻപുള്ള ക്വിസ് ചരിത്രങ്ങൾ ഒന്നുമില്ല.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "ഒരു ദിവസം കളിച്ച് ആദ്യ ക്വിസ് വിലയിരുത്തൽ സൃഷ്‌ടിക്കൂ!",
                                fontSize = 11.sp,
                                color = Color.Gray.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(historyList) { history ->
                    val accuracyPct = (history.score.toFloat() / history.totalQuestions.toFloat() * 100).toInt()
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showHistoryReview(history) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = history.dayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "കളിച്ച തീയതി: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(history.timestamp))}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${history.score}/${history.totalQuestions}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "$accuracyPct% വിജയം",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (accuracyPct >= 80) Color(0xFF00C853) else if (accuracyPct >= 40) Color(0xFFFF9100) else Color.Red
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "ഉωτερങ്ങൾ പരിശോധിക്കാൻ ക്ലിക്ക് ചെയ്യുക",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.deleteHistoryRecord(history.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete record",
                                        tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("ചരിത്രം മായ്‌ക്കുക") },
            text = { Text("നിങ്ങളുടെ പഴയ സ്കോർ ചരിത്രങ്ങളെല്ലാം പൂർണ്ണമായി മായ്‌ക്കാൻ ആഗ്രഹിക്കുന്നുണ്ടോ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showClearConfirm = false
                        Toast.makeText(context, "സ്കോർ ചരിത്രം മായ്‌ച്ചിരിക്കുന്നു", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("അതെ, മായ്‌ക്കുക")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirm = false }) {
                    Text("വേണ്ട")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPrepareScreen(viewModel: MalluQuizViewModel) {
    val day by viewModel.selectedDay.collectAsStateWithLifecycle()
    
    // Choose question size (10, 20, 30, 50 - defaults to 50)
    var selectedCount by remember { mutableIntStateOf(50) }
    // Timer seconds: (15s, 30s, 45s, 60s, 0 is Unlimited)
    var selectedTimerSecs by remember { mutableIntStateOf(30) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ക്വിസ് ക്രമീകരണം", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(QuizScreen.DASHBOARD) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            day?.let { d ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = d.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${viewModel.getMonthNameInMalayalam(d.month)} ${d.day}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = d.celebrationDetails,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Question Count Selector Component
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ചോദ്യങ്ങളുടെ എണ്ണം തിരഞ്ഞെടുക്കുക",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val counts = listOf(10, 20, 30, 50)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        counts.forEach { count ->
                            val isSel = selectedCount == count
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        2.dp,
                                        if (isSel) MaterialTheme.colorScheme.secondary else Color.LightGray.copy(alpha = 0.5f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { selectedCount = count }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$count",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }

            // Timer Selection Option Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ഒരു ചോദ്യത്തിന് എത്ര സമയം നൽകണം?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val timers = listOf(
                        0 to "Unlimited",
                        15 to "15 സെക്കൻഡ്",
                        30 to "30 സെക്കൻഡ്",
                        45 to "45 സെക്കൻഡ്",
                        60 to "60 സെക്കൻഡ്"
                    )

                    timers.forEach { (sec, label) ->
                        val isSel = selectedTimerSecs == sec
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedTimerSecs = sec }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSel,
                                onClick = { selectedTimerSecs = sec }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Hint Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ഉത്തരങ്ങൾ ക്വിസ്സിന്റെ അവസാനം ഫിനിഷ് ചെയ്ത ശേഷം മാത്രമേ കാണിക്കുകയുള്ളൂ, തുടർന്ന് ഓരോ ചോദ്യത്തിന്റെയും പ്രസക്തി വിശദമായി പരിശോധിക്കാവുന്നതാണ്.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.startQuiz(selectedCount, selectedTimerSecs) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .testTag("launch_quiz_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "ക്വിസ് പ്ലേ ചെയ്യുക",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizRunningScreen(viewModel: MalluQuizViewModel) {
    val activeState by viewModel.activeQuizState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        activeState.dayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(QuizScreen.DASHBOARD) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Exit to dashboard")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                activeState.isFetching -> {
                    // Loading State with Traditional Kerala Lamp Theme
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary,
                            strokeWidth = 5.dp,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "ചോദ്യങ്ങൾ തയ്യാറാക്കുന്നു...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gemini AI ചോദ്യങ്ങൾ തരംതിരിക്കുന്നു. അല്പം കാത്തിരിക്കൂ...",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                activeState.fetchError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = activeState.fetchError ?: "",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { viewModel.navigateTo(QuizScreen.DASHBOARD) }) {
                            Text("ഹോമിലേക്ക് മടങ്ങുക")
                        }
                    }
                }

                activeState.questions.isNotEmpty() && activeState.currentQuestionIndex < activeState.questions.size -> {
                    val questionIndex = activeState.currentQuestionIndex
                    val currentQuestion = activeState.questions[questionIndex]

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Progress bar & questions counts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "ചോദ്യം ${questionIndex + 1} / ${activeState.totalQuestions}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            // Remaining timer count
                            if (activeState.timerMaxSeconds > 0) {
                                val remaining = activeState.timerRemainingSeconds
                                val pct = remaining.toFloat() / activeState.timerMaxSeconds.toFloat()
                                val timerColor = if (pct <= 0.3f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = timerColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$remaining സെക്കൻഡ്",
                                        fontWeight = FontWeight.Black,
                                        color = timerColor,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Progress Linear Bar Indicator
                        LinearProgressIndicator(
                            progress = { (questionIndex + 1).toFloat() / activeState.totalQuestions.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )

                        // Main Question Text box card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                Color(0xFF0C462B)
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = currentQuestion.question,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    lineHeight = 25.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Answer Options items (A, B, C, D)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            currentQuestion.options.forEachIndexed { optIdx, optionText ->
                                val label = when(optIdx) {
                                    0 -> "A"
                                    1 -> "B"
                                    2 -> "C"
                                    else -> "D"
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectAnswer(optIdx) }
                                        .testTag("option_card_${label.lowercase()}"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = optionText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Trigger to skip question if desired
                        OutlinedButton(
                            onClick = { viewModel.selectAnswer(-1) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "ഈ ചോദ്യം ഒഴിവാക്കുക (Skip)",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultsScreen(viewModel: MalluQuizViewModel) {
    val activeState by viewModel.activeQuizState.collectAsStateWithLifecycle()
    val score = activeState.score
    val total = activeState.totalQuestions
    
    val accuracy = if (total > 0) (score.toFloat() / total.toFloat() * 100).toInt() else 0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ക്വിസ് ഫലം (Results)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = activeState.dayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "പരിശീലനം വിജയകരമായി പൂർത്തിയാക്കി",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Score circular progress dial
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    // Gray trace track
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.4f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Highlighting color
                    val brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFC7A02C), Color(0xFF0F5A37))
                    )
                    drawArc(
                        brush = brush,
                        startAngle = -90f,
                        sweepAngle = (accuracy / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$accuracy%",
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ശരിയുത്തരങ്ങൾ",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Success feedback texts in Malayalam
            Text(
                text = when {
                    accuracy >= 90 -> "അതിഗംഭീരം! മികച്ച വിജയം."
                    accuracy >= 75 -> "വളരെ നന്നായിരിക്കുന്നു! നന്നായി പഠിച്ചു."
                    accuracy >= 50 -> "കൊള്ളാം! അല്പം കൂടി മെച്ചപ്പെടാം."
                    else -> "പഠനം തുടരൂ! വീണ്ടും പരിശീലിക്കുക."
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats summary layout cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF00C853)
                        )
                        Text(text = "ശരിയുത്തരങ്ങൾ", fontSize = 10.sp, color = Color.Gray)
                    }
                    
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.LightGray))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val wrong = total - score
                        Text(
                            text = "$wrong",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(text = "തെറ്റിയവ/ഒഴിവാക്കിയവ", fontSize = 10.sp, color = Color.Gray)
                    }

                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.LightGray))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$total",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = "ആകെ ചോദ്യങ്ങൾ", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // CTA Action Buttons: Review and Home Backing
            Button(
                onClick = { viewModel.startReview() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("review_answers_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ഉത്തരങ്ങൾ പരിശോധിക്കുക",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { viewModel.navigateTo(QuizScreen.DASHBOARD) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("back_home_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "ഹോം പേജിലേക്ക് മടങ്ങുക",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizReviewScreen(viewModel: MalluQuizViewModel, isPastHistory: Boolean) {
    val activeState by viewModel.activeQuizState.collectAsStateWithLifecycle()
    val historyState by viewModel.reviewingHistory.collectAsStateWithLifecycle()

    val title = if (isPastHistory) historyState?.dayName ?: "" else activeState.dayName
    val dateText = if (isPastHistory) historyState?.dayDate ?: "" else activeState.dayDate
    
    val questions = remember(activeState, historyState, isPastHistory) {
        if (isPastHistory) {
            historyState?.questionsJson?.let { QuizRepository.jsonToQuestions(it) } ?: emptyList()
        } else {
            activeState.questions
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ഉത്തര അവലോകനം (Review)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "$title ($dateText)",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.82f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(QuizScreen.DASHBOARD) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ശരിയായ ഉത്തരങ്ങളും അതിന്റെ ചരിത്ര പശ്ചാത്തലവും ദയവായി താഴെ വായിച്ചു മനസ്സിലാക്കുക. ഇത് ഭാവിയിലെ പരീക്ഷകൾക്കും മത്സരങ്ങൾക്കും സഹായകരമാകും.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            itemsIndexed(questions) { qIndex, q ->
                val isAnswered = q.selectedIndex != -1
                val isCorrect = q.selectedIndex == q.answerIndex

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_card_$qIndex"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title header index correctness tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "ചോദ്യം ${qIndex + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isCorrect -> Color(0xFF00C853).copy(alpha = 0.1f)
                                            !isAnswered -> Color.Gray.copy(alpha = 0.1f)
                                            else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when {
                                        isCorrect -> "ശരിയുത്തരം"
                                        !isAnswered -> "ഉത്തരം നൽകിയിട്ടില്ല"
                                        else -> "തെറ്റായ ഉത്തരം"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isCorrect -> Color(0xFF00C853)
                                        !isAnswered -> Color.Gray
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Question texts
                        Text(
                            text = q.question,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Options highlights
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            q.options.forEachIndexed { opIdx, opText ->
                                val label = when(opIdx) {
                                    0 -> "A"
                                    1 -> "B"
                                    2 -> "C"
                                    else -> "D"
                                }

                                val isChosen = q.selectedIndex == opIdx
                                val isRight = q.answerIndex == opIdx

                                val borderClr = when {
                                    isRight -> Color(0xFF00C853)
                                    isChosen -> MaterialTheme.colorScheme.tertiary
                                    else -> Color.LightGray.copy(alpha = 0.4f)
                                }

                                val containerClr = when {
                                    isRight -> Color(0xFFE8F5E9)
                                    isChosen -> Color(0xFFFFEBEE)
                                    else -> Color.Transparent
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(containerClr)
                                        .border(1.dp, borderClr, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "$label. ",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isRight) Color(0xFF2E7D32) else Color.Gray
                                        )
                                        Text(
                                            text = opText,
                                            fontSize = 13.sp,
                                            fontWeight = if (isRight || isChosen) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isRight) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (isRight) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Correct",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else if (isChosen) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Incorrect",
                                                tint = Color.Red,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Learning Explanation note paragraph in Malayalam
                        if (q.explanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        "പഠന കുറിപ്പ് (Explanation)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = q.explanation,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.navigateTo(QuizScreen.DASHBOARD) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ഹോമിലേക്ക് മടങ്ങുക", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Global Calendar Logic helpers
private fun getDaysInMonth(month: Int, year: Int): Int {
    return when (month) {
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
}

private fun getStartDayOfWeek(month: Int, year: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, 1)
    return cal.get(Calendar.DAY_OF_WEEK)
}
