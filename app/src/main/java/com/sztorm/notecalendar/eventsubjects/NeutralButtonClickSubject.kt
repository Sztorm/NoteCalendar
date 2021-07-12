package com.sztorm.notecalendar.eventsubjects

import android.view.View

interface NeutralButtonClickSubject {
    fun addOnNeutralButtonClickListener(listener: View.OnClickListener): Boolean
    fun removeOnNeutralButtonClickListener(listener: View.OnClickListener): Boolean
    fun clearOnNeutralButtonClickListeners()
}

open class NeutralButtonClickSubjectImpl : NeutralButtonClickSubject  {
    private val clickListeners: MutableSet<View.OnClickListener> = LinkedHashSet()

    fun invokeNeutralButtonClickListeners(view: View) {
        for (listener in clickListeners) {
            listener.onClick(view)
        }
    }

    override fun addOnNeutralButtonClickListener(listener: View.OnClickListener): Boolean
        = clickListeners.add(listener)

    override fun removeOnNeutralButtonClickListener(listener: View.OnClickListener): Boolean
        = clickListeners.remove(listener)

    override fun clearOnNeutralButtonClickListeners() = clickListeners.clear()
}