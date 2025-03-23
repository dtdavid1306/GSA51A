package com.golfapp.gsa51.ui.theme.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.golfapp.gsa51.R
import com.golfapp.gsa51.ui.theme.GSAPurple

/**
 * Shared TopAppBar component for consistent UI across all screens
 *
 * @param title The title text to display
 * @param showBackButton Whether to show the back button (true for all screens except the first one)
 * @param onBackClick Callback when back button is clicked
 * @param onInfoClick Callback when info button is clicked to show game rules
 * @param actions Additional actions to display in the top bar (optional)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GSATopAppBar(
    title: String,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            // Add custom actions first (if any)
            actions()

            // Add info icon as the last action
            IconButton(onClick = onInfoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "Game Rules",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = GSAPurple
        )
    )
}