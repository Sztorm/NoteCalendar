package com.sztorm.notecalendar.helpers

import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment

class DialogFragmentHelper private constructor() {
    companion object {
        fun DialogFragment.setMaximumWidth() {
            val view = requireActivity().window.decorView
            val insets = WindowInsetsCompat
                .toWindowInsetsCompat(view.rootWindowInsets, view)
                .getInsets(WindowInsetsCompat.Type.systemBars())
            val width: Int = ((resources.displayMetrics.widthPixels - insets.right - insets.left))
            val height = ViewGroup.LayoutParams.WRAP_CONTENT

            requireDialog().window!!.setLayout(width, height)
        }
    }
}