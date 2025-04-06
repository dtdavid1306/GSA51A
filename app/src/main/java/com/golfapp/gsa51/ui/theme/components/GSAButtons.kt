package com.golfapp.gsa51.ui.theme.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.golfapp.gsa51.ui.theme.GSAPurple
import kotlinx.coroutines.delay

@Composable
fun GSAPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Add interaction source to detect press state
    val interactionSource = remember { MutableInteractionSource() }
    // Get press state from interaction source
    val isPressed by interactionSource.collectIsPressedAsState()
    // Animate scale based on press state
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "buttonScale"
    )

    // Track if the press is valid (long enough to trigger action)
    var pressedTime by remember { mutableStateOf(0L) }
    var isPressConfirmed by remember { mutableStateOf(false) }

    Button(
        onClick = {
            // We'll manually trigger onClick after checking press duration
            val currentTime = System.currentTimeMillis()
            if (pressedTime == 0L) {
                // First press - record time
                pressedTime = currentTime
            } else {
                // Check if press was long enough
                val pressDuration = currentTime - pressedTime
                if (pressDuration >= 150) { // 150ms minimum press
                    onClick()
                }
                // Reset press tracking
                pressedTime = 0L
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale), // Apply the scale animation
        enabled = enabled,
        interactionSource = interactionSource, // Pass the interaction source
        colors = ButtonDefaults.buttonColors(
            containerColor = GSAPurple,
            disabledContainerColor = GSAPurple.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// Update GSASecondaryButton to include similar functionality
@Composable
fun GSASecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Add interaction source to detect press state
    val interactionSource = remember { MutableInteractionSource() }
    // Get press state from interaction source
    val isPressed by interactionSource.collectIsPressedAsState()
    // Animate scale based on press state
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "buttonScale"
    )

    // Track if the press is valid (long enough to trigger action)
    var pressedTime by remember { mutableStateOf(0L) }

    OutlinedButton(
        onClick = {
            // We'll manually trigger onClick after checking press duration
            val currentTime = System.currentTimeMillis()
            if (pressedTime == 0L) {
                // First press - record time
                pressedTime = currentTime
            } else {
                // Check if press was long enough
                val pressDuration = currentTime - pressedTime
                if (pressDuration >= 150) { // 150ms minimum press
                    onClick()
                }
                // Reset press tracking
                pressedTime = 0L
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale), // Apply the scale animation
        enabled = enabled,
        interactionSource = interactionSource, // Pass the interaction source
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = GSAPurple
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}