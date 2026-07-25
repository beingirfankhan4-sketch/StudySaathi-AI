package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag

sealed class NavItem(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : NavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Assistant : NavItem("assistant", "AI Assistant", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    object Explainer : NavItem("explainer", "Explainer", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    object Quiz : NavItem("quiz", "Quiz & Cards", Icons.Filled.Psychology, Icons.Outlined.Psychology)
    object Notes : NavItem("notes", "Notes", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
}

@Composable
fun StudySaathiBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem.Home,
        NavItem.Assistant,
        NavItem.Explainer,
        NavItem.Quiz,
        NavItem.Notes
    )

    NavigationBar(
        modifier = modifier.testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("nav_item_${item.route}")
            )
        }
    }
}
