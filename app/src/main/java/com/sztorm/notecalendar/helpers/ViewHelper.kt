package com.sztorm.notecalendar.helpers

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

class ViewHelper {
    companion object {
        fun View.hideKeyboard() {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.windowToken, 0)
        }

        fun View.showKeyboard() {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, 0)
        }
    }
}