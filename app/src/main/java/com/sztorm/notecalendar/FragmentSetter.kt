@file:Suppress("unused")

package com.sztorm.notecalendar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentSetter(
    private val fragmentManager: FragmentManager,
    private val fragmentContainerID: Int,
    private val defaultResAnimIn: Int = R.anim.anim_in,
    private val defaultResAnimOut: Int = R.anim.anim_out
) {
    fun setFragment(
        fragment: Fragment,
        resAnimIn: Int = defaultResAnimIn,
        resAnimOut: Int = defaultResAnimOut
    ) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(resAnimIn, resAnimOut)
            .replace(fragmentContainerID, fragment)
            .commit()
    }

    fun setFragment(
        fragment: Fragment,
        backStackTag: String?,
        resAnimIn: Int = defaultResAnimIn,
        resAnimOut: Int = defaultResAnimOut
    ): Int = fragmentManager
        .beginTransaction()
        .setCustomAnimations(resAnimIn, resAnimOut)
        .replace(fragmentContainerID, fragment)
        .addToBackStack(backStackTag)
        .commit()
}