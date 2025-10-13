package com.sztorm.notecalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.sztorm.notecalendar.simplelistpreference.SimpleListDialog
import com.sztorm.notecalendar.simplelistpreference.SimpleListDialogAdapter

class ThemedSimpleListDialog(override var themePainter: ThemePainter) :
    SimpleListDialog(), ThemePaintable {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        val lblTitle: TextView = root.findViewById(R.id.lblTitle)
        val layoutList: ListView = root.findViewById(R.id.layoutList)
        val btnPositive: MaterialButton = root.findViewById(R.id.btnPositive)
        val btnNegative: MaterialButton = root.findViewById(R.id.btnNegative)
        val themeValues: ThemeColors = themePainter.values
        val adapter = (layoutList.adapter as SimpleListDialogAdapter)

        adapter.onCreateViewHolderListener = {
            it.itemLabel.setTextColor(themeValues.textColor)
            themePainter.paintRadio(it.itemRadio)
        }
        root.setBackgroundColor(themeValues.backgroundColor)
        lblTitle.setTextColor(themeValues.textColor)
        themePainter.paintDialogButton(btnPositive)
        themePainter.paintDialogButton(btnNegative)

        return root
    }

    class Builder(private val themePainter: ThemePainter): SimpleListDialog.Builder() {
        override fun build(): ThemedSimpleListDialog = createInstance(this)

        companion object {
            private fun createInstance(options: Builder): ThemedSimpleListDialog {
                val result = ThemedSimpleListDialog(options.themePainter)
                result.mEntries = options.entries
                result.mEntryValues = options.entryValues
                result.mSelectedValue = options.selectedValue
                result.mTitle = options.title
                result.mNegativeButtonText = options.negativeButtonText
                result.mPositiveButtonText = options.positiveButtonText

                return result
            }
        }
    }
}