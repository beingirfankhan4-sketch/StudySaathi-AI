package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<StudyNoteEntity>>

    @Query("SELECT * FROM study_notes WHERE subject = :subject ORDER BY timestamp DESC")
    fun getNotesBySubject(subject: String): Flow<List<StudyNoteEntity>>

    @Query("SELECT * FROM study_notes WHERE title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchNotes(query: String): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNoteEntity): Long

    @Update
    suspend fun updateNote(note: StudyNoteEntity)

    @Delete
    suspend fun deleteNote(note: StudyNoteEntity)

    @Query("DELETE FROM study_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getQuizHistory(): Flow<List<QuizHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizScore(quizScore: QuizHistoryEntity): Long
}
