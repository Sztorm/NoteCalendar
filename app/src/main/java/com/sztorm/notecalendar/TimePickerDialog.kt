package com.sztorm.notecalendar

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.timepicker.TimeFormat
import picker.ugurtekbas.com.Picker.Picker

open class TimePickerDialog: DialogFragment() {
    private val positiveButtonListeners: MutableSet<View.OnClickListener> = LinkedHashSet()
    private val negativeButtonListeners: MutableSet<View.OnClickListener> = LinkedHashSet()
    private val cancelListeners: MutableSet<DialogInterface.OnCancelListener> = LinkedHashSet()
    private val dismissListeners: MutableSet<DialogInterface.OnDismissListener> = LinkedHashSet()
    private val viewCreatedListeners: MutableSet<OnViewCreatedListener> = LinkedHashSet()
    private lateinit var mPicker: Picker
    private var mTitle: CharSequence? = null
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var mTimeFormat: Boolean = false

    val picker: Picker
        get() = mPicker
    val title: CharSequence?
        get() = mTitle
    val timeFormat: Int
        @TimeFormat
        get() = if (mTimeFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

    override fun onStart() {
        super.onStart()
        val view = requireActivity().window.decorView
        val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view).getInsets(
            WindowInsetsCompat.Type.systemBars())
        val width: Int = ((resources.displayMetrics.widthPixels - insets.right - insets.left))
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        requireDialog().window!!.setLayout(width, height)
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
        mPicker.setHourFormat(mTimeFormat)
        mPicker.setTime(mHour, mMinute)
        mPicker.isEnabled = true

        if (mTitle === null) {
            titleHeader.visibility = View.GONE
        }
        else {
            titleHeader.text = mTitle
        }
        positiveButton.setOnClickListener { v ->
            for (listener in positiveButtonListeners) {
                listener.onClick(v)
            }
            dismiss()
        }
        negativeButton.setOnClickListener { v ->
            for (listener in negativeButtonListeners) {
                listener.onClick(v)
            }
            dismiss()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (listener in viewCreatedListeners) {
            listener.onViewCreated(view)
        }
    }

    override fun onCancel(dialogInterface: DialogInterface) {
        for (listener in cancelListeners) {
            listener.onCancel(dialogInterface)
        }
        super.onCancel(dialogInterface)
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        for (listener in dismissListeners) {
            listener.onDismiss(dialogInterface)
        }
        val viewGroup = view as ViewGroup?
        viewGroup?.removeAllViews()

        super.onDismiss(dialogInterface)
    }

    open fun addOnPositiveButtonClickListener(listener: View.OnClickListener): Boolean
        = positiveButtonListeners.add(listener)

    open fun removeOnPositiveButtonClickListener(listener: View.OnClickListener): Boolean
        = positiveButtonListeners.remove(listener)

    open fun clearOnPositiveButtonClickListeners() = positiveButtonListeners.clear()

    open fun addOnNegativeButtonClickListener(listener: View.OnClickListener): Boolean
        = negativeButtonListeners.add(listener)

    open fun removeOnNegativeButtonClickListener(listener: View.OnClickListener): Boolean
        = negativeButtonListeners.remove(listener)

    open fun clearOnNegativeButtonClickListeners() = negativeButtonListeners.clear()

    open fun addOnCancelListener(listener: DialogInterface.OnCancelListener): Boolean
        = cancelListeners.add(listener)

    open fun removeOnCancelListener(listener: DialogInterface.OnCancelListener): Boolean
        = cancelListeners.remove(listener)

    open fun clearOnCancelListeners() = cancelListeners.clear()

    open fun addOnDismissListener(listener: DialogInterface.OnDismissListener): Boolean
        = dismissListeners.add(listener)

    open fun removeOnDismissListener(listener: DialogInterface.OnDismissListener): Boolean
        = dismissListeners.remove(listener)

    open fun addOnViewCreatedClickListener(listener: OnViewCreatedListener): Boolean
        = viewCreatedListeners.add(listener)

    open fun removeOnViewCreatedClickListener(listener: OnViewCreatedListener): Boolean
        = viewCreatedListeners.remove(listener)

    open fun clearOnViewCreatedClickListeners() = viewCreatedListeners.clear()


    open fun clearOnDismissListeners() = dismissListeners.clear()

    interface OnViewCreatedListener {
        fun onViewCreated(view: View)
    }

    class Builder {
        private var hour = 0
        private var minute = 0
        private var titleText: CharSequence? = null
        private var timeFormat: Boolean = false

        fun setHour(@IntRange(from = 0, to = 23) hour: Int): Builder {
            this.hour = hour
            return this
        }

        fun setMinute(@IntRange(from = 0, to = 60) minute: Int): Builder {
            this.minute = minute
            return this
        }

        fun setTimeFormat(@TimeFormat format: Int): Builder {
            timeFormat = format == TimeFormat.CLOCK_24H
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
                result.mTimeFormat = options.timeFormat
                result.mTitle = options.titleText

                return result
            }
        }
    }
}