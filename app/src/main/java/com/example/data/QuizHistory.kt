package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_history")
data class QuizHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayName: String,
    val dayDate: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long,
    val questionsJson: String // Serialized Question list containing user selections for answer review
)
