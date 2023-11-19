package com.sztorm.notecalendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayBinding
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedStringGenitiveCase
import com.sztorm.notecalendar.repositories.NoteRepository
import java.time.LocalDate

class DayFragment : Fragment() {
    private lateinit var binding: FragmentDayBinding
    private lateinit var fragmentSetter: FragmentSetter
    private lateinit var mainActivity: MainActivity
    private lateinit var _swipeListener: SwipeListener
    private var postInitArgs: Arguments? = null
    val swipeListener: SwipeListener
        get() = _swipeListener

    fun postInit(args: Arguments?) {
        postInitArgs = args
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentSetter = FragmentSetter(
            childFragmentManager,
            R.id.dayNoteFragmentContainer,
            R.anim.anim_in_note,
            R.anim.anim_out_note
        )
        binding = FragmentDayBinding.inflate(inflater, container, false)
        _swipeListener = SwipeListener(mainActivity, binding.root.context)
        val viewedDate: LocalDate = mainActivity.sharedData.viewedDate

        setTheme()
        setNoteFragmentOnCreate(viewedDate)
        setTouchListener()
        setLabelsText(viewedDate)

        return binding.root
    }

    fun setFragment(fragment: Fragment) = fragmentSetter.setFragment(fragment)

    private fun setNoteFragmentOnCreate(date: LocalDate) {
        val possibleNote: NoteData? = NoteRepository.getByDate(date)

        if (possibleNote == null) {
            fragmentSetter.setFragment(
                DayNoteEmptyFragment(this, postInitArgs),
                resAnimIn = R.anim.anim_immediate,
                resAnimOut = R.anim.anim_immediate
            )
        } else {
            fragmentSetter.setFragment(
                DayNoteFragment(this, possibleNote, postInitArgs)
            )
        }
        postInitArgs = null
    }

    private fun setLabelsText(date: LocalDate) {
        binding.lblDayOfMonth.text = date.dayOfMonth.toString()
        binding.lblDayOfWeek.text = date.dayOfWeek.toLocalizedString(mainActivity)
        binding.lblMonth.text = date.month.toLocalizedStringGenitiveCase(mainActivity)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener() = binding.root.setOnTouchListener(_swipeListener)

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter
        val themeValues: ThemeValues = themePainter.values

        binding.lblDayOfMonth.setTextColor(themeValues.textColor)
        binding.lblDayOfWeek.setTextColor(themeValues.textColor)
        binding.lblMonth.setTextColor(themeValues.textColor)
    }

    class SwipeListener(
        private val mainActivity: MainActivity, context: Context
    ) : OnSwipeListener(context) {
        override fun onSwipeLeft() {
            super.onSwipeLeft()
            val sharedData = mainActivity.sharedData
            sharedData.viewedDate = sharedData.viewedDate.plusDays(1)
            mainActivity.setMainFragment(
                MainFragmentType.DAY, R.anim.anim_in_left, R.anim.anim_out_left
            )
        }

        override fun onSwipeRight() {
            super.onSwipeRight()
            val sharedData = mainActivity.sharedData
            sharedData.viewedDate = sharedData.viewedDate.minusDays(1)
            mainActivity.setMainFragment(
                MainFragmentType.DAY, R.anim.anim_in_right, R.anim.anim_out_right
            )
        }
    }
}