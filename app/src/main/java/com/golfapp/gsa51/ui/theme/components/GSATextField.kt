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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
    maxValue: Int = Int.MAX_VALUE, // Maximum value parameter
    dropdownEnabled: Boolean = true // New parameter to enable dropdown mode
) {
    // Determine if we have a valid score (non-empty)
    val hasValidInput = remember(value) { derivedStateOf { value.isNotEmpty() } }

    // Animate background color based on input validity
    val backgroundColor by animateColorAsState(
        targetValue = if (hasValidInput.value) Color(0xFFE8F5E9) else Color.Transparent,
        label = "scoreFieldBackground"
    )

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }

    // If dropdown is enabled, use the dropdown version
    if (dropdownEnabled) {
        Box(
            modifier = modifier
                .clip(MaterialTheme.shapes.small)
                .background(backgroundColor)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { /* No direct input in dropdown mode */ },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "#", color = Color.Gray, textAlign = TextAlign.Center) },
                singleLine = true,
                readOnly = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    textAlign = TextAlign.Center
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Score",
                        tint = GSAPurple,
                        modifier = Modifier.clickable { expanded = true }
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = GSAPurple,
                    cursorColor = GSAPurple
                ),
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(100.dp)
            ) {
                // Create score options from 1 to maxValue
                (1..maxValue).forEach { score ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = score.toString(),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        onClick = {
                            onValueChange(score.toString())
                            expanded = false
                        }
                    )
                }
            }
        }
    } else {
        // Original implementation for text input
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
}