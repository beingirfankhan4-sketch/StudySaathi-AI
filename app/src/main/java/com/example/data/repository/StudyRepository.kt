package com.example.data.repository

import com.example.data.local.QuizHistoryEntity
import com.example.data.local.StudyDao
import com.example.data.local.StudyNoteEntity
import com.example.data.remote.GeminiRepository
import kotlinx.coroutines.flow.Flow

class StudyRepository(
    private val studyDao: StudyDao,
    private val geminiRepository: GeminiRepository
) {
    val allNotes: Flow<List<StudyNoteEntity>> = studyDao.getAllNotes()
    val quizHistory: Flow<List<QuizHistoryEntity>> = studyDao.getQuizHistory()

    fun getNotesBySubject(subject: String): Flow<List<StudyNoteEntity>> {
        return if (subject.isBlank() || subject == "All") {
            studyDao.getAllNotes()
        } else {
            studyDao.getNotesBySubject(subject)
        }
    }

    fun searchNotes(query: String): Flow<List<StudyNoteEntity>> {
        return studyDao.searchNotes(query)
    }

    suspend fun saveNote(note: StudyNoteEntity): Long {
        return studyDao.insertNote(note)
    }

    suspend fun updateNote(note: StudyNoteEntity) {
        studyDao.updateNote(note)
    }

    suspend fun deleteNote(note: StudyNoteEntity) {
        studyDao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Long) {
        studyDao.deleteNoteById(id)
    }

    suspend fun saveQuizScore(score: QuizHistoryEntity): Long {
        return studyDao.insertQuizScore(score)
    }

    suspend fun askAiAssistant(prompt: String, mode: String = "Normal"): Result<String> {
        return geminiRepository.generateStudyResponse(prompt = prompt, explanationMode = mode)
    }
}
