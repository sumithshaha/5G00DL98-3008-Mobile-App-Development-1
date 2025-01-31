// File: app/src/main/java/com/example/myapplication/MainActivity.kt
package com.example.myapplication

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Wrap our content in the application theme
            MyApplicationTheme {
                // Create a Material surface as our root container
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call our main composable function
                    HelloWorldScreen()
                }
            }
        }
    }
}

// Our main composable function that contains the UI elements
@Composable
fun HelloWorldScreen() {
    // State to track the counter value
    var counter by remember { mutableStateOf(0) }

    // Column to arrange elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Welcome text
        Text(
            text = "Hello World",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Counter display
        Text(
            text = "You've clicked $counter times",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to increment counter
        Button(
            onClick = { counter++ }
        ) {
            Text("Click me!")
        }
    }
}

// Preview section with multiple preview configurations

// Basic preview with light theme
@Preview(
    name = "Light Mode",
    showBackground = true,
    widthDp = 320,
    heightDp = 640
)
@Composable
fun HelloWorldPreviewLight() {
    MyApplicationTheme(darkTheme = false) {
        HelloWorldScreen()
    }
}

// Dark theme preview
@Preview(
    name = "Dark Mode",
    showBackground = true,
    widthDp = 320,
    heightDp = 640,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HelloWorldPreviewDark() {
    MyApplicationTheme(darkTheme = true) {
        HelloWorldScreen()
    }
}

// Full device preview
@Preview(
    name = "Full Device Preview",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun HelloWorldPreviewFullDevice() {
    MyApplicationTheme {
        HelloWorldScreen()
    }
}