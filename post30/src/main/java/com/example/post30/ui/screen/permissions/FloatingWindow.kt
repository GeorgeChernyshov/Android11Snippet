package com.example.post30.ui.screen.permissions

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.post30.R

class FloatingWindow(val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val rootView = layoutInflater.inflate(R.layout.window, null)

    private val windowParams = WindowManager.LayoutParams(
        0,
        0,
        0,
        0,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        PixelFormat.TRANSLUCENT
    )

    init {
        initWindowParams()
        initWindow()
    }

    fun open() {
        try {
            windowManager.addView(rootView, windowParams)
        } catch (e: Exception) {
            val a = e.message
        }
    }

    fun close() {
        try {
            windowManager.removeView(rootView)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    private fun initWindowParams() {
        calculateSizeAndPosition(windowParams, 300, 80)
    }

    private fun initWindow() {
        // Using kotlin extension for views caused error, so good old findViewById is used
        rootView.setOnClickListener { close() }
    }

    private fun calculateSizeAndPosition(
        params: WindowManager.LayoutParams,
        widthInDp: Int,
        heightInDp: Int
    ) {
        val dm = getCurrentDisplayMetrics()
        // We have to set gravity for which the calculated position is relative.
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.width = (widthInDp * dm.density).toInt()
        params.height = (heightInDp * dm.density).toInt()
        params.x = (dm.widthPixels - params.width) / 2
        params.y = (dm.heightPixels - params.height) / 2
    }

    private fun getCurrentDisplayMetrics(): DisplayMetrics {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm
    }
}