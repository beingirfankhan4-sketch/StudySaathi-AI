package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.StudyDatabase
import com.example.data.remote.GeminiRepository
import com.example.data.repository.StudyRepository
import com.example.ui.components.NavItem
import com.example.ui.components.StudySaathiBottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.StudySaathiTheme
import com.example.ui.viewmodel.StudyViewModel
import com.example.ui.viewmodel.StudyViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = StudyDatabase.getDatabase(applicationContext)
        val geminiRepo = GeminiRepository()
        val studyRepo = StudyRepository(database.studyDao(), geminiRepo)

        val viewModelFactory = StudyViewModelFactory(studyRepo)
        val viewModel = ViewModelProvider(this, viewModelFactory)[StudyViewModel::class.java]

        setContent {
            StudySaathiTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: NavItem.Home.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        StudySaathiBottomNavBar(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(NavItem.Home.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavItem.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavItem.Home.route) {
                            HomeScreen(
                                onNavigate = { route -> navController.navigate(route) },
                                onSelectSubjectPreset = { subject ->
                                    viewModel.setExplainerSubject(subject)
                                    viewModel.setExplainerTopic("$subject core principles & exam overview")
                                }
                            )
                        }

                        composable(NavItem.Assistant.route) {
                            AiAssistantScreen(viewModel = viewModel)
                        }

                        composable(NavItem.Explainer.route) {
                            TopicExplainerScreen(
                                viewModel = viewModel,
                                onNavigateToNotes = { navController.navigate(NavItem.Notes.route) }
                            )
                        }

                        composable(NavItem.Quiz.route) {
                            QuizFlashcardScreen(viewModel = viewModel)
                        }

                        composable(NavItem.Notes.route) {
                            NotesScreen(
                                viewModel = viewModel,
                                onNavigateToExplainer = { navController.navigate(NavItem.Explainer.route) }
                            )
                        }
                    }
                }
            }
        }
    }
}
