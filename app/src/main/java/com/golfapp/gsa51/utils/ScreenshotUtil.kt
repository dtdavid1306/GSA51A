package com.golfapp.gsa51.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ScreenshotUtil {

    // Save bitmap to a temporary file and get its URI for sharing
    fun saveBitmapToTempFile(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val imagesDir = File(context.cacheDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }

        val imageFile = File(imagesDir, fileName)

        return try {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

// This is a new helper function we'll add to ResultsScreen.kt
// It uses callbacks to safely capture screenshots from composable context
@Composable
fun CaptureComposable(
    content: @Composable () -> Unit,
    onCaptured: (Bitmap?) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current.rootView
    val captureRequested = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.onGloballyPositioned { coordinates ->
            if (captureRequested.value) {
                try {
                    val bitmap = Bitmap.createBitmap(
                        coordinates.size.width,
                        coordinates.size.height,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = android.graphics.Canvas(bitmap)
                    view.draw(canvas)
                    onCaptured(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onCaptured(null)
                } finally {
                    captureRequested.value = false
                }
            }
        }
    ) {
        content()
    }
}