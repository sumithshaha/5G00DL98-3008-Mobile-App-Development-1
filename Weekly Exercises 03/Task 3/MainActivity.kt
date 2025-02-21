// 3. - Multi-Screen Navigation in Jetpack Compose
package com.example.composestatemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Set up the navigation app
                    NavigationApp()
                }
            }
        }
    }
}

// Sealed class to define our navigation routes
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail")
}

@Composable
fun NavigationApp() {
    // Create a NavController to handle navigation
    val navController = rememberNavController()

    // Set up the navigation host with the home screen as the start destination
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Define the home screen composable
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = {
                    // Navigate to detail screen
                    navController.navigate(Screen.Detail.route)
                }
            )
        }

        // Define the detail screen composable
        composable(Screen.Detail.route) {
            DetailScreen(
                onNavigateBack = {
                    // Navigate back to previous screen
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToDetail: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display welcome text
        Text(
            text = "Welcome to Home Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        // Add some spacing
        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate to detail screen
        Button(
            onClick = onNavigateToDetail
        ) {
            Text("Go to Details")
        }
    }
}

@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display detail screen text
        Text(
            text = "Detail Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        // Add some spacing
        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate back
        Button(
            onClick = onNavigateBack
        ) {
            Text("Back")
        }
    }
}