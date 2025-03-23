package com.golfapp.gsa51.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ScreenshotUtil {
    /**
     * Captures a view and saves it to a temporary file
     *
     * @param context The application context
     * @param view The View to capture
     * @param filename The name to use for the temporary file
     * @return The Uri of the saved image, or null if capture failed
     */
    fun captureViewToUri(
        context: Context,
        view: View,
        filename: String = "screenshot.png"
    ): Uri? {
        try {
            // Capture the view to a bitmap
            val bitmap = view.drawToBitmap(Bitmap.Config.ARGB_8888)

            // Save the bitmap to a temporary file
            val cachePath = File(context.cacheDir, "screenshots")
            cachePath.mkdirs()

            val file = File(cachePath, filename)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // Get a URI for the file using FileProvider
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e("ScreenshotUtil", "Error capturing view to URI", e)
            return null
        }
    }

    /**
     * Fallback method that creates a basic image with text
     * Used when proper view capture fails
     */
    fun createBasicImage(
        context: Context,
        filename: String = "screenshot.png"
    ): Uri? {
        try {
            // Create a basic bitmap
            val bitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)

            // Draw a white background
            canvas.drawColor(android.graphics.Color.WHITE)

            // Create a text paint
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 30f
            }

            // Draw some basic text
            canvas.drawText("Golf Score Report", 50f, 50f, paint)
            canvas.drawText("See text report for details", 50f, 100f, paint)

            // Save the bitmap to a temporary file
            val cachePath = File(context.cacheDir, "screenshots")
            cachePath.mkdirs()

            val file = File(cachePath, filename)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // Get a URI for the file using FileProvider
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e("ScreenshotUtil", "Error creating basic image", e)
            return null
        }
    }
}