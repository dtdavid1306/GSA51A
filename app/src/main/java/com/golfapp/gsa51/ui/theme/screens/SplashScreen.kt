package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.golfapp.gsa51.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    // Background and content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)), // Golf green background color
        contentAlignment = Alignment.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.gsa_logo_playstore),
            contentDescription = "GSA Logo",
            modifier = Modifier.size(240.dp)
        )
    }

    // Auto-navigate after delay
    LaunchedEffect(key1 = true) {
        delay(2000) // 2-second delay
        onSplashFinished()
    }
}