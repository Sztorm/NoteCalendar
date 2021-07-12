package com.sztorm.notecalendar.simplelistpreference

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.eventsubjects.*
import com.sztorm.notecalendar.helpers.DialogFragmentHelper.Companion.setMaximumWidth

open class SimpleListDialog(
    private val posBtnClickSubject: PositiveButtonClickSubjectImpl = PositiveButtonClickSubjectImpl(),
    private val negBtnClickSubject: NegativeButtonClickSubjectImpl = NegativeButtonClickSubjectImpl(),
    private val dialogCancelSubject: DialogCancelSubjectImpl = DialogCancelSubjectImpl(),
    private val dialogDismissSubject: DialogDismissSubjectImpl = DialogDismissSubjectImpl(),
    private val viewCreatedSubject: ViewCreatedSubjectImpl = ViewCreatedSubjectImpl(),
    private val valueChangeSubject: ValueChangeSubjectImpl<CharSequence?> = ValueChangeSubjectImpl()) :
        DialogFragment(),
        PositiveButtonClickSubject by posBtnClickSubject,
        NegativeButtonClickSubject by negBtnClickSubject,
        DialogCancelSubject by dialogCancelSubject,
        DialogDismissSubject by dialogDismissSubject,
        ViewCreatedSubject by viewCreatedSubject,
        ValueChangeSubject<CharSequence?> by valueChangeSubject {
    protected lateinit var mEntries: Array<CharSequence>
    protected lateinit var mEntryValues: Array<CharSequence>
    protected var mSelectedValue: CharSequence? = null
    protected var mTitle: CharSequence? = null
    protected var mNegativeButtonText: CharSequence? = null
    protected var mPositiveButtonText: CharSequence? = null

    var selectedValue: CharSequence?
        get() = mSelectedValue
        protected set(value) {
            mSelectedValue = value
            valueChangeSubject.invokeValueChangeListeners(value)
        }

    override fun onStart() {
        super.onStart()
        setMaximumWidth()
    }

    private fun handleTitle(title: TextView) {
        if (mTitle === null) {
            title.visibility = View.GONE
            return
        }
        title.text = mTitle
    }

    private fun handlePositiveButton(button: MaterialButton) {
        if (mPositiveButtonText === null) {
            button.visibility = View.GONE
            return
        }
        button.text = mPositiveButtonText
        button.setOnClickListener {
            posBtnClickSubject.invokePositiveButtonClickListeners(it)
            dismiss()
        }
    }

    private fun handleNegativeButton(button: MaterialButton) {
        if (mNegativeButtonText === null) {
            button.visibility = View.GONE
            return
        }
        button.text = mNegativeButtonText
        button.setOnClickListener {
            negBtnClickSubject.invokeNegativeButtonClickListeners(it)
            dismiss()
        }
    }

    private fun handleList(list: ListView) {
        val checkedPosition = if (mSelectedValue === null) -1
            else mEntryValues.indexOfFirst { value -> value == mSelectedValue!! }

        val adapter = SimpleListDialogAdapter(requireContext(), mEntries, checkedPosition)
        adapter.onItemClickListener = { _, position ->
            selectedValue = mEntryValues[position]
        }
        list.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = layoutInflater.inflate(R.layout.fragment_simple_list_dialog, container) as ViewGroup
        val lblTitle: TextView = root.findViewById(R.id.lblTitle)
        val layoutList: ListView = root.findViewById(R.id.layoutList)
        val btnPositive: MaterialButton = root.findViewById(R.id.btnPositive)
        val btnNegative: MaterialButton = root.findViewById(R.id.btnNegative)

        handleTitle(lblTitle)
        handleList(layoutList)
        handlePositiveButton(btnPositive)
        handleNegativeButton(btnNegative)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreatedSubject.invokeViewCreatedListeners(view)
    }

    override fun onCancel(dialogInterface: DialogInterface) {
        dialogCancelSubject.invokeCancelListeners(dialogInterface)
        super.onCancel(dialogInterface)
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        dialogDismissSubject.invokeDismissListeners(dialogInterface)

        val viewGroup = view as ViewGroup?
        viewGroup?.removeAllViews()

        super.onDismiss(dialogInterface)
    }

    open class Builder {
        protected var entries: Array<CharSequence> = emptyArray()
        protected var entryValues: Array<CharSequence> = emptyArray()
        protected var selectedValue: CharSequence? = null
        protected var title: CharSequence? = null
        protected var positiveButtonText: CharSequence? = null
        protected var negativeButtonText: CharSequence? = null

        open fun setSelectedValue(selectedValue: CharSequence?): Builder {
            this.selectedValue = selectedValue
            return this
        }

        open fun setEntries(entries: Array<CharSequence>): Builder {
            this.entries = entries
            return this
        }

        open fun setEntryValues(entryValues: Array<CharSequence>): Builder {
            this.entryValues = entryValues
            return this
        }

        open fun setTitle(title: CharSequence?): Builder {
            this.title = title
            return this
        }

        open fun setPositiveButton(text: CharSequence): Builder {
            this.positiveButtonText = text
            return this
        }

        open fun setNegativeButton(text: CharSequence): Builder {
            this.negativeButtonText = text
            return this
        }

        open fun build(): SimpleListDialog = createInstance(this)

        companion object {
            private fun createInstance(options: Builder): SimpleListDialog {
                val result = SimpleListDialog()
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