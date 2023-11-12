package com.sztorm.notecalendar

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

open class OnSwipeTouchListener(
    ctx: Context?,
    val swipeThreshold: Int = 100,
    val swipeVelocityThreshold: Int = 100
) : OnTouchListener {
    private val gestureDetector: GestureDetector

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            me1: MotionEvent?, me2: MotionEvent, velocityX: Float, velocityY: Float
        ): Boolean {
            if (me1 == null) {
                return false
            }
            val diffY: Float = me2.y - me1.y
            val diffX: Float = me2.x - me1.x

            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    return true
                }
            } else if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                if (diffY > 0) {
                    onSwipeBottom()
                } else {
                    onSwipeTop()
                }
                return true
            }
            return false
        }
    }

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeTop() {}
    open fun onSwipeBottom() {}
}