package com.golfapp.gsa51.ui.theme.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.golfapp.gsa51.ui.theme.GSAPurple
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GSATextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(text = it) } },
        placeholder = placeholder?.let { { Text(text = it) } },
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        readOnly = readOnly,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = GSAPurple,
            cursorColor = GSAPurple
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GSAScoreField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxValue: Int = Int.MAX_VALUE // Add this parameter
) {
    // Determine if we have a valid score (non-empty)
    val hasValidInput = remember(value) { derivedStateOf { value.isNotEmpty() } }

    // Animate background color based on input validity
    val backgroundColor by animateColorAsState(
        targetValue = if (hasValidInput.value) Color(0xFFE8F5E9) else Color.Transparent,
        label = "scoreFieldBackground"
    )

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // For simple validation: If new value is empty, or is a number that's within the limit, accept it
            if (newValue.isEmpty() ||
                (newValue.all { it.isDigit() } && newValue.toIntOrNull()?.let { it <= maxValue } ?: false)) {
                onValueChange(newValue)
            }
            // Otherwise, silently reject (don't update the value)
        },
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor),
        placeholder = { Text(text = "#", color = Color.Gray) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = if (keyboardActions.onNext != null) ImeAction.Next else ImeAction.Done
        ),
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = GSAPurple,
            cursorColor = GSAPurple
        )
    )
}