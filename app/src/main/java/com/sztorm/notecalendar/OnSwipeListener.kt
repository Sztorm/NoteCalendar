@file:Suppress("unused")

package com.sztorm.notecalendar

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.CallSuper
import kotlin.math.abs

open class OnSwipeListener(
    context: Context,
    val swipeThreshold: Int = 100,
    val swipeVelocityThreshold: Int = 100
) : OnTouchListener {
    private val gestureDetector = GestureDetector(context, GestureListener())
    private var swipeListeners = ArrayList<OnDirectionalSwipeListener>()

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
                    onSwipeDown()
                } else {
                    onSwipeUp()
                }
                return true
            }
            return false
        }
    }

    final override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        return gestureDetector.onTouchEvent(event)
    }

    fun addOnSwipeListener(listener: OnDirectionalSwipeListener) {
        swipeListeners.add(listener)
    }

    fun removeOnSwipeListener(listener: OnDirectionalSwipeListener) {
        swipeListeners.remove(listener)
    }

    @CallSuper
    open fun onSwipeLeft() {
        val listeners = swipeListeners.toTypedArray()

        for (listener in listeners) {
            listener.onSwipe(SwipeDirection.Left, this)
        }
    }

    @CallSuper
    open fun onSwipeRight() {
        val listeners = swipeListeners.toTypedArray()

        for (listener in listeners) {
            listener.onSwipe(SwipeDirection.Right, this)
        }
    }

    @CallSuper
    open fun onSwipeUp() {
        val listeners = swipeListeners.toTypedArray()

        for (listener in listeners) {
            listener.onSwipe(SwipeDirection.Up, this)
        }
    }

    @CallSuper
    open fun onSwipeDown() {
        val listeners = swipeListeners.toTypedArray()

        for (listener in listeners) {
            listener.onSwipe(SwipeDirection.Down, this)
        }
    }
}

fun interface OnDirectionalSwipeListener {
    fun onSwipe(direction: SwipeDirection, swipeTouchListener: OnSwipeListener)
}

enum class SwipeDirection {
    Left, Right, Up, Down
}