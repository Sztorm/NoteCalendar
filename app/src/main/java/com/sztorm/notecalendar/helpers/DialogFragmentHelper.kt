package com.sztorm.notecalendar.helpers

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment

class DialogFragmentHelper private constructor() {
    companion object {
        fun DialogFragment.setMaximumWidth() {
            val view: View = requireActivity().window.decorView
            val width: Int = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                ((resources.displayMetrics.widthPixels))
            } else {
                val insets: Insets = WindowInsetsCompat
                    .toWindowInsetsCompat(view.rootWindowInsets, view)
                    .getInsets(WindowInsetsCompat.Type.systemBars())
                ((resources.displayMetrics.widthPixels - insets.right - insets.left))
            }
            val height = ViewGroup.LayoutParams.WRAP_CONTENT

            requireDialog().window!!.setLayout(width, height)
        }
    }
}