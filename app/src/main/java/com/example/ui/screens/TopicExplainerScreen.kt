package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicExplainerScreen(
    viewModel: StudyViewModel,
    onNavigateToNotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.explainerTopic.collectAsState()
    val mode by viewModel.explainerMode.collectAsState()
    val subject by viewModel.explainerSubject.collectAsState()
    val result by viewModel.explainerResult.collectAsState()
    val isLoading by viewModel.isExplainerLoading.collectAsState()
    val isSaved by viewModel.isNoteSaved.collectAsState()

    val subjects = listOf("Science", "Math", "Computer Science", "History", "Literature", "Exam Prep")
    val modes = listOf("Normal", "Simplify (10yo)", "Exam Summary", "Step-by-Step", "Formula & Rules")

    var isSubjectDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("topic_explainer_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Header
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "📖 AI Topic Explainer",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Understand difficult concepts in simple, structured explanations",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        // Input Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Enter Topic or Concept",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { viewModel.setExplainerTopic(it) },
                        placeholder = { Text("e.g. Photosynthesis, Pythagorean Theorem, Newton's Laws") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("explainer_topic_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (topic.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setExplainerTopic("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Subject Selector Dropdown / Row
                    Text(
                        text = "Select Subject:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(subjects) { s ->
                            FilterChip(
                                selected = subject == s,
                                onClick = { viewModel.setExplainerSubject(s) },
                                label = { Text(s, fontSize = 12.sp) },
                                modifier = Modifier.testTag("explainer_subject_$s")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Explanation Style Selector
                    Text(
                        text = "Explanation Style:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(modes) { m ->
                            FilterChip(
                                selected = mode == m,
                                onClick = { viewModel.setExplainerMode(m) },
                                label = { Text(m, fontSize = 12.sp) },
                                modifier = Modifier.testTag("explainer_style_$m")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { viewModel.explainTopic() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("explain_topic_button"),
                        enabled = topic.isNotBlank() && !isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generating AI Explanation...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Explain with StudySaathi AI")
                        }
                    }
                }
            }
        }

        // Result Card
        if (result != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = subject,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = AmberAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = mode,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AmberAccent
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        SelectionContainer {
                            Text(
                                text = result ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (isSaved) {
                                Button(
                                    onClick = { onNavigateToNotes() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("view_saved_notes_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Saved! View Notes")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.saveExplainerAsNote() },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("save_note_button")
                                ) {
                                    Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save to Study Notes")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
