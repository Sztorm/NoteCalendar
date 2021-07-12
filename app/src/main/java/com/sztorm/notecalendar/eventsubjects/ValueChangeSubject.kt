package com.sztorm.notecalendar.eventsubjects

interface ValueChangeSubject<T> {
    fun addOnValueChangeListener(listener: (T) -> Unit): Boolean
    fun removeOnValueChangeListener(listener: (T) -> Unit): Boolean
    fun clearOnValueChangeListeners()
}

open class ValueChangeSubjectImpl<T> : ValueChangeSubject<T>  {
    private val valueChangeListeners: MutableSet<(T) -> Unit> = LinkedHashSet()

    fun invokeValueChangeListeners(value: T) {
        for (listener in valueChangeListeners) {
            listener(value)
        }
    }

    override fun addOnValueChangeListener(listener: (T) -> Unit): Boolean
        = valueChangeListeners.add(listener)

    override fun removeOnValueChangeListener(listener: (T) -> Unit): Boolean
        = valueChangeListeners.remove(listener)

    override fun clearOnValueChangeListeners() = valueChangeListeners.clear()
}