package com.example.post30.ui.screen.newfeatures.graphics

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.createBitmap

class NDKImageLoader(private val context: Context) {

    private external fun decodeImageFromAsset(
        assetManager: AssetManager,
        assetFileName: String,
        bitmap: Bitmap
    ): Boolean

    init {
        System.loadLibrary("ndk_image_decoder")
    }

    fun loadBitmap(assetFileName: String): Bitmap? {
        val outputBitmap = createBitmap(1200, 600)

        try {
            // Call the native function
            val success = decodeImageFromAsset(
                context.assets,
                assetFileName,
                outputBitmap
            )

            if (success) {
                Log.d(TAG, "Image decoded successfully from NDK.")
                return outputBitmap
            } else {
                Log.e(TAG, "Failed to decode image from NDK.")
                outputBitmap.recycle()
            }
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "UnsatisfiedLinkError: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}")
        }

        return null
    }

    companion object {
        private const val TAG = "NDKImageLoader"
    }
}