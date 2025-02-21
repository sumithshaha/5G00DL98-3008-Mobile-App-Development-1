// 5. - Navigation with Bottom Navigation Bar
package com.example.composestatemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Set up our main app structure with bottom navigation
                BottomNavApp()
            }
        }
    }
}

// Sealed class to define our navigation items with their properties
sealed class NavigationItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    // Define each navigation item with its properties
    object Home : NavigationItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Profile : NavigationItem(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    object Settings : NavigationItem(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}

@Composable
fun BottomNavApp() {
    // Create NavController to manage navigation
    val navController = rememberNavController()

    // Get current navigation state
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Create Scaffold with bottom navigation
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Create navigation items for each screen
                listOf(
                    NavigationItem.Home,
                    NavigationItem.Profile,
                    NavigationItem.Settings
                ).forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == item.route) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            // Handle navigation with proper back stack management
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when navigating back
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // Set up navigation host with screens
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavigationItem.Home.route) {
                HomeScreen()
            }
            composable(NavigationItem.Profile.route) {
                ProfileScreen()
            }
            composable(NavigationItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    // Home screen content
    CenteredScreenContent(
        title = "Home Screen",
        description = "Welcome to the main screen of the app"
    )
}

@Composable
fun ProfileScreen() {
    // Profile screen content
    CenteredScreenContent(
        title = "Profile Screen",
        description = "View and edit your profile here"
    )
}

@Composable
fun SettingsScreen() {
    // Settings screen content
    CenteredScreenContent(
        title = "Settings Screen",
        description = "Configure app settings and preferences"
    )
}

@Composable
fun CenteredScreenContent(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}