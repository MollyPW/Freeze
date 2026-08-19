package com.example.freeze

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView

class FreezeAccessibilityService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var floatingButtonView: View? = null
    private var freezeOverlayView: View? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showFloatingButton()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Required override, not used
    }

    override fun onInterrupt() {
        // Required override, not used
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showFloatingButton() {
        if (floatingButtonView != null) return

        val button = TextView(this).apply {
            text = "Pause"
            setBackgroundColor(Color.parseColor("#CC2196F3"))
            setTextColor(Color.WHITE)
            setPadding(40, 24, 40, 24)
            textSize = 14f
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 300

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var downTime = 0L
        var moved = false

        button.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    downTime = System.currentTimeMillis()
                    moved = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) moved = true
                    params.x = initialX + dx
                    params.y = initialY + dy
                    windowManager.updateViewLayout(button, params)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val pressDuration = System.currentTimeMillis() - downTime
                    if (!moved) {
                        if (pressDuration >= 600) {
                            hideFloatingButton()
                        } else {
                            showFreezeOverlay()
                        }
                    }
                    true
                }

                else -> false
            }
        }

        floatingButtonView = button
        windowManager.addView(button, params)
    }

    private fun hideFloatingButton() {
        floatingButtonView?.let {
            windowManager.removeView(it)
            floatingButtonView = null
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showFreezeOverlay() {
        if (freezeOverlayView != null) return

        val overlay = View(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
        }

        var pressStartTime = 0L
        overlay.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressStartTime = System.currentTimeMillis()
                }

                MotionEvent.ACTION_UP -> {
                    if (System.currentTimeMillis() - pressStartTime >= 5000) {
                        removeFreezeOverlay()
                    }
                }
            }
            true
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        freezeOverlayView = overlay
        windowManager.addView(overlay, params)
        hideFloatingButton()
    }

    private fun removeFreezeOverlay() {
        freezeOverlayView?.let {
            windowManager.removeView(it)
            freezeOverlayView = null
        }
        showFloatingButton()
    }

}