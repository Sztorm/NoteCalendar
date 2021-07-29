package com.sztorm.notecalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * [Fragment] which represents settings of the application.
 *
 * Use the [SettingsFragment.createInstance] factory method to
 * create an instance of this fragment.
 **/
class SettingsFragment : Fragment() {
    enum class SettingsFragmentType {
        ROOT,
        CUSTOM_THEME;

        companion object {
            private val VALUES: Array<SettingsFragmentType> = values()

            fun from(ordinal: Int) =
                try { VALUES[ordinal] }
                catch (e: IndexOutOfBoundsException) {
                    throw IllegalArgumentException("Value is out of range of enum ordinals. The " +
                            "value must be in [0, 1] range.")
                }
        }
    }

    private lateinit var fragmentSetter: FragmentSetter
    private var fragmentType: SettingsFragmentType = SettingsFragmentType.ROOT

    fun <T> setFragment (fragment: T, fragmentType: SettingsFragmentType)
        where T : Fragment {
        this.fragmentType = fragmentType
        fragmentSetter.setFragment(fragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        fragmentSetter = FragmentSetter(
            requireActivity().supportFragmentManager,
            R.id.innerSettingsFragmentContainer,
            R.anim.anim_in,
            R.anim.anim_out)

        val fragment: Fragment = when (fragmentType) {
            SettingsFragmentType.ROOT -> RootSettingsFragment.createInstance(this)
            else -> CustomThemeSettingsFragment.createInstance(this)
        }
        setFragment(fragment, fragmentType)

        return view
    }

    companion object : MainFragmentCreator<SettingsFragment> {
        @JvmStatic
        override fun createInstance(): SettingsFragment = SettingsFragment()

        fun createInstance(fragmentType: SettingsFragmentType): SettingsFragment {
            val result = SettingsFragment()
            result.fragmentType = fragmentType

            return result
        }

        @JvmStatic
        override val fragmentType: MainActivity.MainFragmentType
            = MainActivity.MainFragmentType.SETTINGS_FRAGMENT
    }
}