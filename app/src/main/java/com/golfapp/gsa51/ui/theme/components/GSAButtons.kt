package com.golfapp.gsa51.ui.theme.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.golfapp.gsa51.ui.theme.GSAPurple
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

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

    Button(
        onClick = onClick,
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

// Update GSASecondaryButton to include animation
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

    OutlinedButton(
        onClick = onClick,
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