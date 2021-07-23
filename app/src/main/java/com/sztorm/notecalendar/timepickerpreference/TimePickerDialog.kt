package com.sztorm.notecalendar.timepickerpreference

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.timepicker.TimeFormat
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.eventsubjects.*
import com.sztorm.notecalendar.helpers.DialogFragmentHelper.Companion.setMaximumWidth
import com.sztorm.timepicker.TwoStepTimePicker

open class TimePickerDialog(
    private val positiveBtnSubject: PositiveButtonClickSubjectImpl = PositiveButtonClickSubjectImpl(),
    private val negativeBtnSubject: NegativeButtonClickSubjectImpl = NegativeButtonClickSubjectImpl(),
    private val dialogCancelSubject: DialogCancelSubjectImpl = DialogCancelSubjectImpl(),
    private val dialogDismissSubject: DialogDismissSubjectImpl = DialogDismissSubjectImpl(),
    private val viewCreatedSubject: ViewCreatedSubjectImpl = ViewCreatedSubjectImpl()) :
        DialogFragment(),
        PositiveButtonClickSubject by positiveBtnSubject,
        NegativeButtonClickSubject by negativeBtnSubject,
        DialogCancelSubject by dialogCancelSubject,
        DialogDismissSubject by dialogDismissSubject,
        ViewCreatedSubject by viewCreatedSubject {
    private lateinit var mPicker: TwoStepTimePicker
    private var mTitle: CharSequence? = null
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var is24Hour: Boolean = false

    val picker: TwoStepTimePicker
        get() = mPicker
    val title: CharSequence?
        get() = mTitle
    val timeFormat: Int
        @TimeFormat
        get() = if (is24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

    override fun onStart() {
        super.onStart()
        setMaximumWidth()
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        val root = layoutInflater.inflate(R.layout.fragment_timepicker, viewGroup) as ViewGroup
        val titleHeader: TextView = root.findViewById(R.id.lblTitle)
        val positiveButton: MaterialButton = root.findViewById(R.id.btnPositive)
        val negativeButton: MaterialButton = root.findViewById(R.id.btnNegative)
        mPicker = root.findViewById(R.id.timePicker)
        mPicker.setTime(mHour, mMinute)
        mPicker.is24Hour = is24Hour
        mPicker.isEnabled = true

        if (mTitle === null) {
            titleHeader.visibility = View.GONE
        }
        else {
            titleHeader.text = mTitle
        }
        positiveButton.setOnClickListener { v ->
            positiveBtnSubject.invokePositiveButtonClickListeners(v)
            dismiss()
        }
        negativeButton.setOnClickListener { v ->
            negativeBtnSubject.invokeNegativeButtonClickListeners(v)
            dismiss()
        }
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

    class Builder {
        private var hour = 0
        private var minute = 0
        private var titleText: CharSequence? = null
        private var is24Hour: Boolean = false

        fun setHour(@IntRange(from = 0, to = 23) hour: Int): Builder {
            this.hour = hour
            return this
        }

        fun setMinute(@IntRange(from = 0, to = 60) minute: Int): Builder {
            this.minute = minute
            return this
        }

        fun setTimeFormat(@TimeFormat format: Int): Builder {
            is24Hour = format == TimeFormat.CLOCK_24H
            return this
        }

        fun setTitleText(charSequence: CharSequence?): Builder {
            titleText = charSequence
            return this
        }

        fun build(): TimePickerDialog {
            return createInstance(this)
        }

        companion object {
            private fun createInstance(options: Builder): TimePickerDialog {
                val result = TimePickerDialog()
                result.mHour = options.hour
                result.mMinute = options.minute
                result.is24Hour = options.is24Hour
                result.mTitle = options.titleText

                return result
            }
        }
    }
}