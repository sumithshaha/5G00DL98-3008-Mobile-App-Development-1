// 2. - Activity Lifecycle and State Handling
package com.example.composestatemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class TimerViewModel : ViewModel() {
    // State flows for UI updates
    private val _elapsedSeconds = MutableStateFlow(0)
    private val _isTimerRunning = MutableStateFlow(true)
    private val _isUserPaused = MutableStateFlow(false)

    // Public read-only access to state flows
    val elapsedSeconds = _elapsedSeconds.asStateFlow()
    val isTimerRunning = _isTimerRunning.asStateFlow()
    val isUserPaused = _isUserPaused.asStateFlow()

    // Time tracking variables
    private var baseElapsedTime: Long = 0      // Total time accumulated before current run
    private var currentStartTime: Long? = null  // Start time of current run
    private var pausedTimestamp: Long? = null   // When the timer was last paused

    init {
        // Initialize the timer when ViewModel is created
        startTiming()
    }

    private fun startTiming() {
        currentStartTime = System.currentTimeMillis()
    }

    fun updateElapsedSeconds() {
        currentStartTime?.let { startTime ->
            val currentElapsed = if (_isTimerRunning.value) {
                // If running, calculate current elapsed time and add to base time
                baseElapsedTime + ((System.currentTimeMillis() - startTime) / 1000)
            } else {
                // If paused, just return the base elapsed time
                baseElapsedTime
            }
            _elapsedSeconds.value = currentElapsed.toInt()
        }
    }

    fun toggleUserPause() {
        _isUserPaused.value = !_isUserPaused.value
        if (_isUserPaused.value) {
            pauseTimer()
        } else {
            resumeTimer()
        }
    }

    fun handleLifecyclePause() {
        if (!_isUserPaused.value) {
            pauseTimer()
        }
    }

    fun handleLifecycleResume() {
        if (!_isUserPaused.value) {
            resumeTimer()
        }
    }

    private fun pauseTimer() {
        if (_isTimerRunning.value) {
            _isTimerRunning.value = false
            currentStartTime?.let { startTime ->
                // Save accumulated time when pausing
                baseElapsedTime += (System.currentTimeMillis() - startTime) / 1000
            }
            pausedTimestamp = System.currentTimeMillis()
            currentStartTime = null
        }
    }

    private fun resumeTimer() {
        if (!_isTimerRunning.value) {
            _isTimerRunning.value = true
            startTiming() // Start a new timing session
            pausedTimestamp = null
        }
    }

    fun resetTimer() {
        _isTimerRunning.value = true
        _isUserPaused.value = false
        baseElapsedTime = 0
        startTiming()
        pausedTimestamp = null
        _elapsedSeconds.value = 0
    }

    // Getter for pause timestamp (used for UI display)
    fun getPausedTimestamp(): Long? = pausedTimestamp
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LifecycleAwareTimerScreen()
                }
            }
        }
    }
}

@Composable
fun LifecycleAwareTimerScreen() {
    val timerViewModel: TimerViewModel = viewModel()

    val elapsedSeconds by timerViewModel.elapsedSeconds.collectAsState()
    val isTimerRunning by timerViewModel.isTimerRunning.collectAsState()
    val isUserPaused by timerViewModel.isUserPaused.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> timerViewModel.handleLifecyclePause()
                Lifecycle.Event.ON_RESUME -> timerViewModel.handleLifecycleResume()
                else -> { /* Handle other lifecycle events if needed */ }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Timer update effect
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            timerViewModel.updateElapsedSeconds()
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = formatTime(elapsedSeconds),
            style = MaterialTheme.typography.displayLarge
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { timerViewModel.toggleUserPause() }
            ) {
                Text(if (!isUserPaused) "Pause" else "Resume")
            }

            Button(
                onClick = { timerViewModel.resetTimer() }
            ) {
                Text("Reset")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Timer Status",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "State: ${when {
                        isUserPaused -> "Paused by User"
                        isTimerRunning -> "Running"
                        else -> "Paused by System"
                    }}",
                    style = MaterialTheme.typography.bodyMedium
                )
                timerViewModel.getPausedTimestamp()?.let {
                    Text(
                        "Paused at: ${formatDateTime(it)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}