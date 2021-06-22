package com.sztorm.notecalendar

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference

open class ConfirmationPreference: Preference {
    protected var mConfirmationDialog: AlertDialog = createConfirmationDialog()

    val confirmationDialog: AlertDialog
        get() = mConfirmationDialog

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) :
            super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) :
            super(context, attributeSet, defStyle)

    private fun createConfirmationDialog(): AlertDialog {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(context.getString(R.string.Settings_DeleteAllNotes_Alert_Title))
        dialog.setMessage(context.getString(R.string.Settings_DeleteAllNotes_Alert_Message))
        dialog.setCancelable(true)
        dialog.setPositiveButton(context.getString(R.string.Confirm)) { _, _ -> }
        dialog.setNegativeButton(context.getString(R.string.Cancel)) {
            dlg, which -> dlg.cancel()
        }
        return dialog.create()
    }

    override fun onClick() {
        mConfirmationDialog.show()
    }
}