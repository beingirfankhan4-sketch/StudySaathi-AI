package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String, // Math, Science, History, Computer Science, Literature, General
    val summary: String,
    val detailedContent: String,
    val keyTakeaways: String, // Comma or newline separated
    val status: String = "Needs Review", // "Needs Review", "Mastered", "Exam Priority"
    val timestamp: Long = System.currentTimeMillis()
)
