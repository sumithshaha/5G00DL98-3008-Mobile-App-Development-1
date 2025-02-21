// 4. - Passing Data Between Screens
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Navigation()
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{userName}") {
        fun createRoute(userName: String) = "detail/$userName"
    }
}

@Composable
fun Navigation() {
    // Create a SnackbarHostState to manage Snackbar messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Create a coroutine scope that's tied to this composable
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToDetail = { userName ->
                        navController.navigate(Screen.Detail.createRoute(userName))
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("userName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userName = backStackEntry.arguments?.getString("userName") ?: ""

                DetailScreen(
                    userName = userName,
                    onNavigateBack = {
                        navController.popBackStack()
                        // Launch a coroutine to show the Snackbar
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Welcome back, $userName!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit
) {
    // Use rememberSaveable to preserve state during configuration changes
    var userName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Home Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Enter your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNavigateToDetail(userName) },
            enabled = userName.isNotBlank(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Go to Details")
        }
    }
}

@Composable
fun DetailScreen(
    userName: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello, $userName!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back")
        }
    }
}