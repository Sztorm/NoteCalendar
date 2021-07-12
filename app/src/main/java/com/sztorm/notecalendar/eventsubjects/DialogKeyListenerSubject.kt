package com.sztorm.notecalendar.eventsubjects

import android.content.DialogInterface
import android.view.KeyEvent

interface DialogKeySubject {
    fun addOnKeyListener(listener: DialogInterface.OnKeyListener): Boolean
    fun removeOnKeyListener(listener: DialogInterface.OnKeyListener): Boolean
    fun clearOnKeyListeners()
}

open class DialogKeySubjectImpl : DialogKeySubject  {
    private val keyListeners: MutableSet<DialogInterface.OnKeyListener> = LinkedHashSet()

    fun invokeKeyListeners(dialog: DialogInterface, keyCode: Int, event: KeyEvent) {
        for (listener in keyListeners) {
            listener.onKey(dialog, keyCode, event)
        }
    }

    override fun addOnKeyListener(listener: DialogInterface.OnKeyListener): Boolean
        = keyListeners.add(listener)

    override fun removeOnKeyListener(listener: DialogInterface.OnKeyListener): Boolean
        = keyListeners.remove(listener)

    override fun clearOnKeyListeners() = keyListeners.clear()
}