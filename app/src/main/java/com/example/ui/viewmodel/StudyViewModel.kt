package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class Flashcard(
    val id: Int,
    val question: String,
    val answer: String,
    val subject: String,
    var isFlipped: Boolean = false
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

class StudyViewModel(private val repository: StudyRepository) : ViewModel() {

    // --- Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Namaste! 👋 I'm StudySaathi AI, your personal intelligent study companion. Ask me any question, request topic explanations, or select a subject below to start learning!",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // --- Notes State ---
    private val _selectedSubject = MutableStateFlow("All")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val notes: StateFlow<List<StudyNoteEntity>> = combine(
        repository.allNotes,
        _selectedSubject,
        _searchQuery
    ) { allNotesList, subject, query ->
        allNotesList.filter { note ->
            val matchesSubject = (subject == "All" || note.subject.equals(subject, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.summary.contains(query, ignoreCase = true) ||
                    note.subject.contains(query, ignoreCase = true)
            matchesSubject && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Topic Explainer State ---
    private val _explainerTopic = MutableStateFlow("")
    val explainerTopic: StateFlow<String> = _explainerTopic.asStateFlow()

    private val _explainerMode = MutableStateFlow("Normal")
    val explainerMode: StateFlow<String> = _explainerMode.asStateFlow()

    private val _explainerSubject = MutableStateFlow("General")
    val explainerSubject: StateFlow<String> = _explainerSubject.asStateFlow()

    private val _explainerResult = MutableStateFlow<String?>(null)
    val explainerResult: StateFlow<String?> = _explainerResult.asStateFlow()

    private val _isExplainerLoading = MutableStateFlow(false)
    val isExplainerLoading: StateFlow<Boolean> = _isExplainerLoading.asStateFlow()

    private val _isNoteSaved = MutableStateFlow(false)
    val isNoteSaved: StateFlow<Boolean> = _isNoteSaved.asStateFlow()

    // --- Flashcard & Quiz State ---
    private val _currentFlashcards = MutableStateFlow<List<Flashcard>>(getDefaultFlashcards())
    val currentFlashcards: StateFlow<List<Flashcard>> = _currentFlashcards.asStateFlow()

    private val _currentQuiz = MutableStateFlow<List<QuizQuestion>>(getDefaultQuiz("General"))
    val currentQuiz: StateFlow<List<QuizQuestion>> = _currentQuiz.asStateFlow()

    private val _userQuizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // Question ID -> Selected Index
    val userQuizAnswers: StateFlow<Map<Int, Int>> = _userQuizAnswers.asStateFlow()

    private val _quizSubmitted = MutableStateFlow(false)
    val quizSubmitted: StateFlow<Boolean> = _quizSubmitted.asStateFlow()

    val quizHistory = repository.quizHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Actions ---

    fun sendChatMessage(userText: String, mode: String = "Normal") {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(text = userText, isUser = true)
        _chatMessages.update { it + userMsg }
        _isChatLoading.value = true

        viewModelScope.launch {
            val result = repository.askAiAssistant(userText, mode)
            _isChatLoading.value = false
            val aiResponse = result.getOrDefault("Sorry, I could not process that request right now.")
            _chatMessages.update { it + ChatMessage(text = aiResponse, isUser = false) }
        }
    }

    fun setExplainerTopic(topic: String) {
        _explainerTopic.value = topic
    }

    fun setExplainerMode(mode: String) {
        _explainerMode.value = mode
    }

    fun setExplainerSubject(subject: String) {
        _explainerSubject.value = subject
    }

    fun explainTopic() {
        val topic = _explainerTopic.value
        if (topic.isBlank()) return

        _isExplainerLoading.value = true
        _explainerResult.value = null
        _isNoteSaved.value = false

        viewModelScope.launch {
            val mode = _explainerMode.value
            val result = repository.askAiAssistant(
                prompt = "Explain the topic '$topic' in detail for a student. Include key concepts, formula or steps if applicable, real world analogy, and 3 key exam takeaways.",
                mode = mode
            )
            _isExplainerLoading.value = false
            _explainerResult.value = result.getOrDefault("Failed to generate explanation.")
        }
    }

    fun saveExplainerAsNote() {
        val topic = _explainerTopic.value
        val result = _explainerResult.value
        if (topic.isBlank() || result.isNullOrBlank()) return

        val subject = _explainerSubject.value
        val summary = if (result.length > 150) result.substring(0, 150) + "..." else result

        val note = StudyNoteEntity(
            title = topic,
            subject = subject,
            summary = summary,
            detailedContent = result,
            keyTakeaways = "Generated with StudySaathi AI ($subject)",
            status = "Needs Review"
        )

        viewModelScope.launch {
            repository.saveNote(note)
            _isNoteSaved.value = true
        }
    }

    fun setSelectedSubjectFilter(subject: String) {
        _selectedSubject.value = subject
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateNoteStatus(note: StudyNoteEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateNote(note.copy(status = newStatus))
        }
    }

    fun deleteNote(note: StudyNoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun selectQuizAnswer(questionId: Int, optionIndex: Int) {
        if (_quizSubmitted.value) return
        _userQuizAnswers.update { it + (questionId to optionIndex) }
    }

    fun submitQuiz(topic: String, subject: String) {
        val answers = _userQuizAnswers.value
        val questions = _currentQuiz.value
        var correctCount = 0

        questions.forEach { q ->
            if (answers[q.id] == q.correctIndex) {
                correctCount++
            }
        }

        _quizSubmitted.value = true

        viewModelScope.launch {
            repository.saveQuizScore(
                QuizHistoryEntity(
                    topic = topic.ifBlank { "General Quiz" },
                    subject = subject,
                    score = correctCount,
                    totalQuestions = questions.size
                )
            )
        }
    }

    fun resetQuiz(subject: String) {
        _currentQuiz.value = getDefaultQuiz(subject)
        _userQuizAnswers.value = emptyMap()
        _quizSubmitted.value = false
    }

    fun toggleFlashcardFlip(cardId: Int) {
        _currentFlashcards.update { cards ->
            cards.map { c ->
                if (c.id == cardId) c.copy(isFlipped = !c.isFlipped) else c
            }
        }
    }

    private fun getDefaultFlashcards(): List<Flashcard> {
        return listOf(
            Flashcard(1, "What is Photosynthesis?", "Process green plants use to synthesize nutrients from CO₂ & water using sunlight.", "Science"),
            Flashcard(2, "State Ohm's Law.", "Voltage (V) = Current (I) × Resistance (R).", "Physics"),
            Flashcard(3, "What is a Prime Number?", "A whole number greater than 1 whose only factors are 1 and itself.", "Math"),
            Flashcard(4, "Define Mitosis.", "Cell division process resulting in 2 genetically identical daughter cells.", "Biology"),
            Flashcard(5, "What is the CPU?", "Central Processing Unit - the brain of the computer that executes instructions.", "Computer Science")
        )
    }

    private fun getDefaultQuiz(subject: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "Which organelle is known as the powerhouse of the cell?",
                options = listOf("Nucleus", "Mitochondria", "Ribosome", "Endoplasmic Reticulum"),
                correctIndex = 1,
                explanation = "Mitochondria produce ATP (energy) needed for cellular functions."
            ),
            QuizQuestion(
                id = 2,
                question = "What is the formula for the area of a circle?",
                options = listOf("2πr", "πr²", "4πr²", "½bh"),
                correctIndex = 1,
                explanation = "Area = π × (radius)²"
            ),
            QuizQuestion(
                id = 3,
                question = "In Python, which keyword defines a function?",
                options = listOf("func", "def", "function", "lambda"),
                correctIndex = 1,
                explanation = "The `def` keyword introduces a function definition in Python."
            )
        )
    }
}
