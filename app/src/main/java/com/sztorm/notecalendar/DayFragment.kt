package com.sztorm.notecalendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayBinding
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedStringGenitiveCase
import com.sztorm.notecalendar.repositories.NoteRepository
import java.time.LocalDate

/**
 * [Fragment] which represents day with notes etc in calendar.
 * Use the [DayFragment.createInstance] factory method to
 * create an instance of this fragment.
 */
class DayFragment : Fragment() {
    private lateinit var binding: FragmentDayBinding
    private lateinit var fragmentSetter: FragmentSetter
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    fun <T, TCreator> setFragment (fragment: T)
        where T : Fragment, TCreator : InstanceCreator<T> {
        fragmentSetter.setFragment(fragment)
    }

    private fun setLabelsText(date: LocalDate) {
        binding.lblDayOfMonth.text = date.dayOfMonth.toString()
        binding.lblDayOfWeek.text = date.dayOfWeek.toLocalizedString(mainActivity)
        binding.lblMonth.text = date.month.toLocalizedStringGenitiveCase(mainActivity)
    }

    private fun handleBtnNoteAddClickEvent() = binding.btnNoteAdd.setOnClickListener {
        binding.btnNoteAdd.isVisible = false
        fragmentSetter.setFragment(DayNoteAddFragment.createInstance(this))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleTouchEvent() = binding.root.setOnTouchListener(
        object : OnSwipeTouchListener(binding.root.context) {

        override fun onSwipeLeft() {
            val date: LocalDate = mainActivity.viewedDate.plusDays(1)
            mainActivity.setMainFragment(
                DayFragment, R.anim.anim_in_left, R.anim.anim_out_left, date)
        }

        override fun onSwipeRight() {
            val date: LocalDate = mainActivity.viewedDate.minusDays(1)
            mainActivity.setMainFragment(
                DayFragment, R.anim.anim_in_right, R.anim.anim_out_right, date)
        }
    })

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter
        val themeValues: ThemeValues = themePainter.values

        themePainter.paintButton(binding.btnNoteAdd)
        binding.lblDayOfMonth.setTextColor(themeValues.textColor)
        binding.lblDayOfWeek.setTextColor(themeValues.textColor)
        binding.lblMonth.setTextColor(themeValues.textColor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSetter = FragmentSetter(
            childFragmentManager,
            R.id.dayNoteFragmentContainer,
            R.anim.anim_in_note,
            R.anim.anim_out_note)
        binding = FragmentDayBinding.inflate(inflater, container, false)
        setTheme()

        val viewedDate: LocalDate = mainActivity.viewedDate
        val possibleNote: NoteData? = NoteRepository.getByDate(viewedDate)

        if (possibleNote == null) {
            binding.btnNoteAdd.isVisible = true
        }
        else {
            binding.btnNoteAdd.isVisible = false
            fragmentSetter.setFragment(
                DayNoteFragment.createInstance(this, possibleNote))
        }
        handleTouchEvent()
        handleBtnNoteAddClickEvent()
        setLabelsText(viewedDate)
        return binding.root
    }

    companion object : MainFragmentCreator<DayFragment> {
        @JvmStatic
        override fun createInstance(): DayFragment = DayFragment()

        @JvmStatic
        override val fragmentType: MainActivity.MainFragmentType
                = MainActivity.MainFragmentType.DAY_FRAGMENT
    }
}