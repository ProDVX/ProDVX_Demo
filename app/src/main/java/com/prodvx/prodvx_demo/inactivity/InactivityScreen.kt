package com.prodvx.prodvx_demo.inactivity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun InactivityScreen(
    inactivityTimerViewModel: InactivityTimerViewModel = viewModel()
) {
    // Observe the lifecycle of the current Composable (which is tied to the Activity's lifecycle)
    val lifecycleOwner = LocalLifecycleOwner.current

    // Use a DisposableEffect to manage the timer based on lifecycle events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Log.d("InactivityScreen", "ON_RESUME: Starting timer.")
                    inactivityTimerViewModel.startTimer()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("InactivityScreen", "ON_PAUSE: Stopping timer.")
                    inactivityTimerViewModel.stopTimer()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            Log.d("InactivityScreen", "DisposableEffect onDispose: Removing observer and stopping timer.")
            lifecycleOwner.lifecycle.removeObserver(observer)
            inactivityTimerViewModel.stopTimer() // Ensure timer is stopped on dispose
        }
    }

    // Observe the elapsed time from the ViewModel for UI display
    val elapsedTime by inactivityTimerViewModel.elapsedTimeSinceLastInteraction

    // Use Modifier.pointerInput to detect any touch events on the entire screen
    // and reset the timer. This is a common way to detect general user interaction in Compose.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                // This block will execute whenever a pointer input (e.g., touch) occurs
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent() // Wait for any pointer event
                        inactivityTimerViewModel.resetInactivityTimer() // Reset timer on interaction
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Inactivity Timer Demo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Last Interaction: ${elapsedTime / 1000} seconds ago", // Display in seconds
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Current Timeout set to ${InactivityTimerViewModel.INACTIVITY_TIMEOUT_S}"
            )
            Text(
                text = "Move your finger or click anywhere on this screen to reset the timer.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}