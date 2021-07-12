package com.sztorm.notecalendar.eventsubjects

import android.view.View

interface PositiveButtonClickSubject {
    fun addOnPositiveButtonClickListener(listener: View.OnClickListener): Boolean
    fun removeOnPositiveButtonClickListener(listener: View.OnClickListener): Boolean
    fun clearOnPositiveButtonClickListeners()
}

open class PositiveButtonClickSubjectImpl : PositiveButtonClickSubject  {
    private val clickListeners: MutableSet<View.OnClickListener> = LinkedHashSet()

    fun invokePositiveButtonClickListeners(view: View) {
        for (listener in clickListeners) {
            listener.onClick(view)
        }
    }

    override fun addOnPositiveButtonClickListener(listener: View.OnClickListener): Boolean
        = clickListeners.add(listener)

    override fun removeOnPositiveButtonClickListener(listener: View.OnClickListener): Boolean
        = clickListeners.remove(listener)

    override fun clearOnPositiveButtonClickListeners() = clickListeners.clear()
}