package com.sztorm.notecalendar.eventsubjects

import android.content.DialogInterface

interface DialogClickSubject {
    fun addOnClickListener(listener: DialogInterface.OnClickListener): Boolean
    fun removeOnClickListener(listener: DialogInterface.OnClickListener): Boolean
    fun clearOnClickListeners()
}

open class DialogClickSubjectImpl : DialogClickSubject  {
    private val clickListeners: MutableSet<DialogInterface.OnClickListener> = LinkedHashSet()

    fun invokeClickListeners(dialog: DialogInterface, which: Int) {
        for (listener in clickListeners) {
            listener.onClick(dialog, which)
        }
    }

    override fun addOnClickListener(listener: DialogInterface.OnClickListener): Boolean
        = clickListeners.add(listener)

    override fun removeOnClickListener(listener: DialogInterface.OnClickListener): Boolean
        = clickListeners.remove(listener)

    override fun clearOnClickListeners() = clickListeners.clear()
}