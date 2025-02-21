// 1. - Understanding State in Jetpack Compose with a Simple Profile Form
package com.example.composestatemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Using Surface to provide proper Material theming and background
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfileFormScreen()
                }
            }
        }
    }
}

@Composable
fun ProfileFormScreen() {
    // State declarations are grouped together for better organization
    // Non-persistent states using remember
    var count by remember { mutableIntStateOf(0) }
    var userName by remember { mutableStateOf("") }

    // Persistent states using rememberSaveable
    var persistentCount by rememberSaveable { mutableIntStateOf(0) }
    var persistentUserName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Non-persistent input section
        Text("Non-persistent Input Demo")
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Enter your name (will be lost on rotation)") }
        )
        Text(
            text = "Hello, ${userName.takeIf { it.isNotEmpty() } ?: "Guest"}!",
            style = MaterialTheme.typography.bodyLarge
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Persistent input section
        Text("Persistent Input Demo")
        TextField(
            value = persistentUserName,
            onValueChange = { persistentUserName = it },
            label = { Text("Enter your name (persists on rotation)") }
        )
        Text(
            text = "Hello, ${persistentUserName.takeIf { it.isNotEmpty() } ?: "Guest"}!",
            style = MaterialTheme.typography.bodyLarge
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Non-persistent counter section
        Text(
            text = "Non-persistent Counter: $count",
            style = MaterialTheme.typography.bodyLarge
        )
        CounterButtons(
            onIncrement = { count++ },
            onReset = { count = 0 }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Persistent counter section
        Text(
            text = "Persistent Counter: $persistentCount",
            style = MaterialTheme.typography.bodyLarge
        )
        CounterButtons(
            onIncrement = { persistentCount++ },
            onReset = { persistentCount = 0 }
        )
    }
}

@Composable
private fun CounterButtons(
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onIncrement) {
            Text("Increment")
        }
        Button(onClick = onReset) {
            Text("Reset")
        }
    }
}