package com.sztorm.notecalendar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentSetter(
        private val fragmentManager : FragmentManager,
        private val fragmentContainerID: Int,
        private val defaultResAnimIn: Int = R.anim.anim_in,
        private val defaultResAnimOut: Int = R.anim.anim_out) {

    fun <T, TCreator> setFragment (
        fragmentCreator: TCreator,
        resAnimIn: Int = defaultResAnimIn,
        resAnimOut: Int = defaultResAnimOut)
        where T : Fragment, TCreator : InstanceCreator<T> {
        val fragment: T = fragmentCreator.createInstance()
        val transaction = fragmentManager.beginTransaction()

        transaction.setCustomAnimations(resAnimIn, resAnimOut)
        transaction.replace(fragmentContainerID, fragment)
        transaction.commit()
    }

    fun <T> setFragment (
        fragment: T,
        resAnimIn: Int = defaultResAnimIn,
        resAnimOut: Int = defaultResAnimOut)
        where T : Fragment {
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(resAnimIn, resAnimOut)
        transaction.replace(fragmentContainerID, fragment)
        transaction.commit()
    }
}