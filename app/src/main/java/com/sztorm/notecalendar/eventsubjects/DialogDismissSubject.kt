package com.sztorm.notecalendar.eventsubjects

import android.content.DialogInterface

interface DialogDismissSubject {
    fun addOnDismissListener(listener: DialogInterface.OnDismissListener): Boolean
    fun removeOnDismissListener(listener: DialogInterface.OnDismissListener): Boolean
    fun clearOnDismissListeners()
}

open class DialogDismissSubjectImpl : DialogDismissSubject  {
    private val dismissListeners: MutableSet<DialogInterface.OnDismissListener> = LinkedHashSet()

    fun invokeDismissListeners(dialog: DialogInterface) {
        for (listener in dismissListeners) {
            listener.onDismiss(dialog)
        }
    }

    override fun addOnDismissListener(listener: DialogInterface.OnDismissListener): Boolean
        = dismissListeners.add(listener)

    override fun removeOnDismissListener(listener: DialogInterface.OnDismissListener): Boolean
        = dismissListeners.remove(listener)

    override fun clearOnDismissListeners() = dismissListeners.clear()
}