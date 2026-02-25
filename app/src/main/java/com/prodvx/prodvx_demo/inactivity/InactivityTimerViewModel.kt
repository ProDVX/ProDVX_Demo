package com.prodvx.prodvx_demo.inactivity

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.prodvx.prodvx_demo.api.sleepDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- Inactivity Timer ViewModel ---
// This ViewModel will manage the timer logic and expose its state.
class InactivityTimerViewModel : androidx.lifecycle.ViewModel() {

    private val TAG = "InactivityTimerVM"

    companion object {
        const val INACTIVITY_TIMEOUT_S = 15
    }

    // Inactivity timeout in millisecond
    private val mInactivityTimeoutMs = INACTIVITY_TIMEOUT_S * 1000L
    // Interval for updating the UI display of elapsed time (e.g., 1 second)
    private val mUiUpdateIntervalMs = 1000L // 1 second

    // Handler for the core inactivity detection timer
    private val inactivityHandler = Handler(Looper.getMainLooper())
    // Runnable for the core inactivity detection timer
    private val inactivityRunnable = Runnable {
        Log.d(TAG, "Inactivity timeout reached. Calling local API.")
        callLocalApi()
    }

    // Handler for periodically updating the UI display of elapsed time
    private val displayUpdateHandler = Handler(Looper.getMainLooper())
    // Runnable for periodically updating the UI display of elapsed time
    private val displayUpdateRunnable = object : Runnable {
        override fun run() {
            // Calculate elapsed time since last interaction and update the state
            _elapsedTimeSinceLastInteraction.value = System.currentTimeMillis() - _lastInteractionTime.value
            // Schedule this runnable again after UI_UPDATE_INTERVAL_MS
            displayUpdateHandler.postDelayed(this, mUiUpdateIntervalMs)
        }
    }

    // Mutable state to store the actual timestamp of the last interaction
    private val _lastInteractionTime = mutableStateOf(System.currentTimeMillis())
    // Mutable state to observe the elapsed time for UI display
    private val _elapsedTimeSinceLastInteraction = mutableStateOf(0L)
    val elapsedTimeSinceLastInteraction: MutableState<Long> = _elapsedTimeSinceLastInteraction


    // Function to reset the inactivity timer and update interaction time
    fun resetInactivityTimer() {
        // Reset the core inactivity timer
        inactivityHandler.removeCallbacks(inactivityRunnable)
        inactivityHandler.postDelayed(inactivityRunnable, mInactivityTimeoutMs)

        // Update the last interaction timestamp
        _lastInteractionTime.value = System.currentTimeMillis()
        // Immediately update the displayed elapsed time
        _elapsedTimeSinceLastInteraction.value = 0L

        Log.d(TAG, "Inactivity timer reset. Next API call check in $mInactivityTimeoutMs ms.")
    }

    // Lifecycle aware start/stop of the timer
    fun startTimer() {
        Log.d(TAG, "ViewModel: Starting timers.")
        resetInactivityTimer() // Start/reset the core inactivity timer
        // Start the display update timer
        displayUpdateHandler.removeCallbacks(displayUpdateRunnable)
        displayUpdateHandler.postDelayed(displayUpdateRunnable, mUiUpdateIntervalMs)
    }

    fun stopTimer() {
        Log.d(TAG, "ViewModel: Stopping timers.")
        // Stop the core inactivity timer
        inactivityHandler.removeCallbacks(inactivityRunnable)
        // Stop the display update timer
        displayUpdateHandler.removeCallbacks(displayUpdateRunnable)
    }

    // This function will be called when the ViewModel is no longer used and is about to be destroyed.
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared. Removing all timer callbacks.")
        inactivityHandler.removeCallbacks(inactivityRunnable)
        displayUpdateHandler.removeCallbacks(displayUpdateRunnable)
    }

    // --- Your Custom API Call Function ---
    private fun callLocalApi() {
        Log.i(TAG, "--- Executing custom API call due to inactivity from ViewModel! ---")
        // TODO: Implement your actual API call logic here.

        CoroutineScope(Dispatchers.IO).launch {
            try {
                sleepDevice()
                Log.d(TAG, "Simulating API call success.")
            } catch (e: Exception) {
                Log.e(TAG, "Simulating API call failed: ${e.message}")
            }
        }
    }
}