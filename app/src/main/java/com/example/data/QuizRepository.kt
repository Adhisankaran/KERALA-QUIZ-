package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class QuizRepository(private val quizHistoryDao: QuizHistoryDao) {

    val allHistory: Flow<List<QuizHistory>> = quizHistoryDao.getAllHistory()

    suspend fun insertQuiz(history: QuizHistory) = withContext(Dispatchers.IO) {
        quizHistoryDao.insertHistory(history)
    }

    suspend fun deleteQuizById(id: Int) = withContext(Dispatchers.IO) {
        quizHistoryDao.deleteHistoryById(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        quizHistoryDao.clearAllHistory()
    }

    // OkHttpClient with 60s timeouts as requested by guidelines
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Dynamically generates a quiz on any celebrated day using Gemini 3.5 Flash API.
     * If the API key is empty/not configured, or if the request fails, it falls back
     * to the offline high-quality fallback database.
     */
    suspend fun generateQuiz(
        dayId: Int,
        dayName: String,
        dayDateString: String,
        questionCount: Int
    ): List<Question> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("QuizRepository", "Gemini API key is not configured. Falling back to offline quiz.")
            return@withContext DayData.getFallbackQuizForDay(dayId, questionCount)
        }

        // Refine system instruction and prompt for best Malayali questions
        val systemInstruction = "You are an expert Kerala history, culture, and Malayalam general knowledge quiz master."
        val prompt = """
            Generate a JSON array of quiz questions in Malayalam about the celebrated day: '$dayName' which is observed on '$dayDateString'.
            The questions should focus on the significance of this day in Kerala, or general international/national significance and how it relates to Kerala/Malayalis.
            Each question must have exactly 4 options and have a correct 'answerIndex' pointing to the correct option index (0 to 3).
            You MUST generate exactly $questionCount multiple-choice questions.
            Ensure the language used is pure, formal, and grammatically correct Malayalam.
            
            Return ONLY a raw JSON array matching this structure, with no markdown block formatting or styling:
            [
              {
                "question": "ചോദ്യം?",
                "options": ["ഓപ്ഷൻ എ", "ഓപ്ഷൻ ബി", "ഓപ്ഷൻ സി", "ഓപ്ഷൻ ഡി"],
                "answerIndex": 0,
                "explanation": "ഈ ചോദ്യത്തിന്റെ ശരിയായ ഉത്തരത്തിന്റെ കാരണം വിശദീകരിക്കുന്ന കുറിപ്പ് മലയാളത്തിൽ."
              }
            ]
            Do not include any conversational intro or extro. Only return valid parseable JSON.
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstruction)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestJson.toString().toRequestBody(mediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("HTTP error ${response.code}: ${response.message}")
                }
                val bodyString = response.body?.string() ?: throw IOException("Empty response body")
                val responseJson = JSONObject(bodyString)
                val textResponse = responseJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val cleanerJson = cleanJsonString(textResponse)
                val jsonArray = JSONArray(cleanerJson)
                val questionsList = mutableListOf<Question>()
                
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val qText = obj.getString("question")
                    val optsArr = obj.getJSONArray("options")
                    val optsList = mutableListOf<String>()
                    for (j in 0 until optsArr.length()) {
                        optsList.add(optsArr.getString(j))
                    }
                    val ansIdx = obj.getInt("answerIndex")
                    val expl = obj.optString("explanation", "")
                    questionsList.add(Question(qText, optsList, ansIdx, expl))
                }

                if (questionsList.isNotEmpty()) {
                    Log.d("QuizRepository", "Successfully fetched ${questionsList.size} custom Malayalam questions from Gemini API.")
                    return@withContext questionsList
                } else {
                    throw Exception("No parsed questions found.")
                }
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error invoking Gemini API: ${e.message}. Swerving to offline backup quiz.", e)
            return@withContext DayData.getFallbackQuizForDay(dayId, questionCount)
        }
    }

    private fun cleanJsonString(raw: String): String {
        var cleaned = raw.trim()
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substringAfter("\n")
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substringBeforeLast("```")
            }
            if (cleaned.startsWith("json")) {
                cleaned = cleaned.removePrefix("json").trim()
            }
        }
        return cleaned.trim()
    }

    companion object {
        fun questionsToJson(questions: List<Question>): String {
            val array = JSONArray()
            for (q in questions) {
                val obj = JSONObject()
                obj.put("question", q.question)
                val opts = JSONArray()
                q.options.forEach { opts.put(it) }
                obj.put("options", opts)
                obj.put("answerIndex", q.answerIndex)
                obj.put("explanation", q.explanation)
                obj.put("selectedIndex", q.selectedIndex)
                array.put(obj)
            }
            return array.toString()
        }

        fun jsonToQuestions(jsonStr: String): List<Question> {
            val list = mutableListOf<Question>()
            if (jsonStr.isBlank()) return list
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val question = obj.getString("question")
                    val optsArray = obj.getJSONArray("options")
                    val options = mutableListOf<String>()
                    for (j in 0 until optsArray.length()) {
                        options.add(optsArray.getString(j))
                    }
                    val answerIndex = obj.getInt("answerIndex")
                    val explanation = obj.optString("explanation", "")
                    val selectedIndex = obj.optInt("selectedIndex", -1)
                    list.add(Question(question, options, answerIndex, explanation, selectedIndex))
                }
            } catch (e: Exception) {
                Log.e("QuizRepository", "Error converting json to questions: ${e.message}", e)
            }
            return list
        }
    }
}
