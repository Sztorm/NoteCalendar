package com.sztorm.notecalendar.eventsubjects

import android.view.View

interface ViewCreatedSubject {
    fun addOnViewCreatedListener(listener: (View) -> Unit): Boolean
    fun removeOnViewCreatedListener(listener: (View) -> Unit): Boolean
    fun clearOnViewCreatedListeners()
}

open class ViewCreatedSubjectImpl : ViewCreatedSubject  {
    private val viewCreatedListeners: MutableSet<(View) -> Unit> = LinkedHashSet()

    fun invokeViewCreatedListeners(view: View) {
        for (listener in viewCreatedListeners) {
            listener(view)
        }
    }

    override fun addOnViewCreatedListener(listener: (View) -> Unit): Boolean
        = viewCreatedListeners.add(listener)

    override fun removeOnViewCreatedListener(listener: (View) -> Unit): Boolean
        = viewCreatedListeners.remove(listener)

    override fun clearOnViewCreatedListeners() = viewCreatedListeners.clear()
}