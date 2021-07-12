package com.sztorm.notecalendar.eventsubjects

import android.view.View

interface NegativeButtonClickSubject {
    fun addOnNegativeButtonClickListener(listener: View.OnClickListener): Boolean
    fun removeOnNegativeButtonClickListener(listener: View.OnClickListener): Boolean
    fun clearOnNegativeButtonClickListeners()
}

open class NegativeButtonClickSubjectImpl : NegativeButtonClickSubject  {
    private val clickListeners: MutableSet<View.OnClickListener> = LinkedHashSet()

    fun invokeNegativeButtonClickListeners(view: View) {
        for (listener in clickListeners) {
            listener.onClick(view)
        }
    }

    override fun addOnNegativeButtonClickListener(listener: View.OnClickListener): Boolean
        = clickListeners.add(listener)

    override fun removeOnNegativeButtonClickListener(listener: View.OnClickListener): Boolean
        = clickListeners.remove(listener)

    override fun clearOnNegativeButtonClickListeners() = clickListeners.clear()
}