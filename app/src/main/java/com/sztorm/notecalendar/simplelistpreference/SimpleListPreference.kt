@file:Suppress("MemberVisibilityCanBePrivate")

package com.sztorm.notecalendar.simplelistpreference

import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.sztorm.notecalendar.R

open class SimpleListPreference: Preference, View.OnClickListener {
    private lateinit var sharedPrefs: SharedPreferences
    protected lateinit var entries: Array<CharSequence>
    protected lateinit var entryValues: Array<CharSequence>
    protected var mDefaultValue: CharSequence? = null
    protected var mValue: CharSequence? = null

    var value: CharSequence?
        get() = mValue
        set(value) {
            mValue = value
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setup(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) :
        super(context, attributeSet, defStyle) {
        setup(context, attributeSet)
    }

    private fun setup(context: Context, attributeSet: AttributeSet) {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attributeSet, R.styleable.CustomizableListPreference)
        try {
            entries = typedArray.getTextArray(
                R.styleable.CustomizableListPreference_android_entries)
            entryValues = typedArray.getTextArray(
                R.styleable.CustomizableListPreference_android_entryValues)
            mDefaultValue = typedArray.getText(
                R.styleable.CustomizableListPreference_android_defaultValue)
        }
        finally {
            typedArray.recycle()
        }
    }

    override fun setDefaultValue(defaultValue: Any?) {
        mDefaultValue = defaultValue as CharSequence?
        super.setDefaultValue(mDefaultValue)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        mValue = sharedPrefs.getString(key, mDefaultValue?.toString())

        val settingView: View = holder.itemView

        settingView.setOnClickListener(this)
    }

    protected open fun setupDialogBuilder() = SimpleListDialog.Builder()
        .setTitle(title)
        .setPositiveButton(context.getString(android.R.string.ok))
        .setNegativeButton(context.getString(android.R.string.cancel))
        .setEntries(entries)
        .setEntryValues(entryValues)
        .setSelectedValue(mValue)

    override fun onClick(v: View) {
        val activity = context as FragmentActivity
        val dialog: SimpleListDialog = setupDialogBuilder().build()

        dialog.addOnPositiveButtonClickListener{
            mValue = dialog.selectedValue

            sharedPrefs.edit {
                putString(key, mValue?.toString())
            }
            onPreferenceChangeListener?.onPreferenceChange(this, mValue)
        }
        dialog.show(activity.supportFragmentManager, null)
    }
}