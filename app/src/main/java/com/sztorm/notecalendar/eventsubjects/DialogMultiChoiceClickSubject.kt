package com.sztorm.notecalendar.eventsubjects

import android.content.DialogInterface

interface DialogMultiChoiceClickSubject {
    fun addOnMultiChoiceClickListener(listener: DialogInterface.OnMultiChoiceClickListener): Boolean
    fun removeOnMultiChoiceClickListener(
        listener: DialogInterface.OnMultiChoiceClickListener): Boolean
    fun clearOnMultiChoiceClickListeners()
}

open class DialogMultiChoiceClickSubjectImpl : DialogMultiChoiceClickSubject  {
    private val multiChoiceClickListeners: MutableSet<DialogInterface.OnMultiChoiceClickListener>
        = LinkedHashSet()

    fun invokeMultiChoiceClickListeners(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        for (listener in multiChoiceClickListeners) {
            listener.onClick(dialog, which, isChecked)
        }
    }

    override fun addOnMultiChoiceClickListener(
        listener: DialogInterface.OnMultiChoiceClickListener): Boolean
        = multiChoiceClickListeners.add(listener)

    override fun removeOnMultiChoiceClickListener(
        listener: DialogInterface.OnMultiChoiceClickListener): Boolean
        = multiChoiceClickListeners.remove(listener)

    override fun clearOnMultiChoiceClickListeners() = multiChoiceClickListeners.clear()
}