package com.sztorm.notecalendar.eventsubjects

import android.content.DialogInterface

interface DialogCancelSubject {
    fun addOnCancelListener(listener: DialogInterface.OnCancelListener): Boolean
    fun removeOnCancelListener(listener: DialogInterface.OnCancelListener): Boolean
    fun clearOnCancelListeners()
}

open class DialogCancelSubjectImpl : DialogCancelSubject  {
    private val cancelListeners: MutableSet<DialogInterface.OnCancelListener> = LinkedHashSet()

    fun invokeCancelListeners(dialog: DialogInterface) {
        for (listener in cancelListeners) {
            listener.onCancel(dialog)
        }
    }

    override fun addOnCancelListener(listener: DialogInterface.OnCancelListener): Boolean
        = cancelListeners.add(listener)

    override fun removeOnCancelListener(listener: DialogInterface.OnCancelListener): Boolean
        = cancelListeners.remove(listener)

    override fun clearOnCancelListeners() = cancelListeners.clear()
}