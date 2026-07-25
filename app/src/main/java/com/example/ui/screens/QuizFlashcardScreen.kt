package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.Flashcard
import com.example.ui.viewmodel.QuizQuestion
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizFlashcardScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Flashcards, 1 = Quiz

    val flashcards by viewModel.currentFlashcards.collectAsState()
    val quizQuestions by viewModel.currentQuiz.collectAsState()
    val userAnswers by viewModel.userQuizAnswers.collectAsState()
    val isSubmitted by viewModel.quizSubmitted.collectAsState()
    val quizHistory by viewModel.quizHistory.collectAsState()

    var currentCardIndex by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_flashcard_screen")
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "🎯 AI Quiz & Flashcards",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Revise key terms and practice exam questions",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .testTag("tab_flashcards")
                    ) {
                        Text(
                            text = "Flashcards",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .testTag("tab_practice_quiz")
                    ) {
                        Text(
                            text = "Practice Quiz",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // Flashcards Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (flashcards.isNotEmpty()) {
                    val safeIndex = currentCardIndex.coerceIn(0, flashcards.size - 1)
                    val card = flashcards[safeIndex]

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Card ${safeIndex + 1} of ${flashcards.size}",
                                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = card.subject,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Flip Flashcard Card Component
                        FlashcardFlipView(
                            card = card,
                            onFlip = { viewModel.toggleFlashcardFlip(card.id) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "👆 Tap card to flip between Question and Answer",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    // Navigation Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (currentCardIndex > 0) currentCardIndex--
                            },
                            enabled = currentCardIndex > 0,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("prev_flashcard_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous")
                        }

                        Button(
                            onClick = {
                                if (currentCardIndex < flashcards.size - 1) {
                                    currentCardIndex++
                                } else {
                                    currentCardIndex = 0 // Loop
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("next_flashcard_button")
                        ) {
                            Text(if (currentCardIndex < flashcards.size - 1) "Next" else "Restart Deck")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        } else {
            // Practice Quiz Section
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isSubmitted) {
                    item {
                        var score = 0
                        quizQuestions.forEach { q ->
                            if (userAnswers[q.id] == q.correctIndex) score++
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Quiz Completed! 🎉",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Your Score: $score / ${quizQuestions.size}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.resetQuiz("General") },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("retake_quiz_button")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retake Quiz")
                                }
                            }
                        }
                    }
                }

                items(quizQuestions) { question ->
                    QuizQuestionCard(
                        question = question,
                        selectedOption = userAnswers[question.id],
                        isSubmitted = isSubmitted,
                        onOptionSelect = { optionIdx ->
                            viewModel.selectQuizAnswer(question.id, optionIdx)
                        }
                    )
                }

                if (!isSubmitted) {
                    item {
                        Button(
                            onClick = { viewModel.submitQuiz("General Practice", "General") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_quiz_button"),
                            enabled = userAnswers.size == quizQuestions.size,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Submit Quiz & Check Results")
                        }
                    }
                }

                // Previous Quiz Scores History
                if (quizHistory.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent Quiz Scores History",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    items(quizHistory) { history ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = history.topic,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = history.subject,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                Surface(
                                    color = TealAccent.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "${history.score}/${history.totalQuestions}",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = TealAccent,
                                            fontWeight = FontWeight.Bold
                                        )
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

@Composable
fun FlashcardFlipView(
    card: Flashcard,
    onFlip: () -> Unit
) {
    Card(
        onClick = onFlip,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .testTag("flashcard_item_${card.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isFlipped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = if (card.isFlipped) AmberAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (card.isFlipped) "ANSWER" else "QUESTION",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (card.isFlipped) AmberAccent else MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (card.isFlipped) card.answer else card.question,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@Composable
fun QuizQuestionCard(
    question: QuizQuestion,
    selectedOption: Int?,
    isSubmitted: Boolean,
    onOptionSelect: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_question_${question.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Q${question.id}. ${question.question}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEachIndexed { idx, optionText ->
                val isSelected = selectedOption == idx
                val isCorrect = question.correctIndex == idx

                val containerColor = when {
                    isSubmitted && isCorrect -> TealAccent.copy(alpha = 0.2f)
                    isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                }

                Surface(
                    onClick = { onOptionSelect(idx) },
                    enabled = !isSubmitted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = containerColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onOptionSelect(idx) },
                            enabled = !isSubmitted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            if (isSubmitted) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "💡 Explanation: ${question.explanation}",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}
