package com.sztorm.notecalendar.eventsubjects

import android.content.DialogInterface

interface DialogShowSubject {
    fun addOnShowListener(listener: DialogInterface.OnShowListener): Boolean
    fun removeOnShowListener(listener: DialogInterface.OnShowListener): Boolean
    fun clearOnShowListeners()
}

open class DialogShowSubjectImpl : DialogShowSubject  {
    private val showListeners: MutableSet<DialogInterface.OnShowListener> = LinkedHashSet()

    fun invokeShowListeners(dialog: DialogInterface) {
        for (listener in showListeners) {
            listener.onShow(dialog)
        }
    }

    override fun addOnShowListener(listener: DialogInterface.OnShowListener): Boolean
        = showListeners.add(listener)

    override fun removeOnShowListener(listener: DialogInterface.OnShowListener): Boolean
        = showListeners.remove(listener)

    override fun clearOnShowListeners() = showListeners.clear()
}