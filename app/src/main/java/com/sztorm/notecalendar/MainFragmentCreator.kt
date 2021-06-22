package com.sztorm.notecalendar

import androidx.fragment.app.Fragment

interface MainFragmentCreator<T> : InstanceCreator<T>
    where T : Fragment {
    val fragmentType: MainActivity.MainFragmentType
}