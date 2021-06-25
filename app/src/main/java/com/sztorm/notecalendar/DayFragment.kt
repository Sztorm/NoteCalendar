package com.sztorm.notecalendar

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedStringGenitiveCase
import kotlinx.android.synthetic.main.fragment_day.view.*
import java.time.LocalDate

/**
 * [Fragment] which represents day with notes etc in calendar.
 * Use the [DayFragment.createInstance] factory method to
 * create an instance of this fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)
class DayFragment : Fragment() {
    private lateinit var fragmentSetter: FragmentSetter
    private lateinit var mView: View
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
        mView.lblDayOfMonth.text = date.dayOfMonth.toString()
        mView.lblDayOfWeek.text = date.dayOfWeek.toLocalizedString(mView.context)
        mView.lblMonth.text = date.month.toLocalizedStringGenitiveCase(mView.context)
    }

    private fun handleBtnNoteAddClickEvent() = mView.btnNoteAdd.setOnClickListener {
        mView.btnNoteAdd.isVisible = false
        fragmentSetter.setFragment(DayNoteAddFragment.createInstance(this))
    }

    private fun handleTouchEvent() = mView.setOnTouchListener(
        object : OnSwipeTouchListener(mView.context) {

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

        themePainter.paintButton(mView.btnNoteAdd)
        mView.lblDayOfMonth.setTextColor(themeValues.textColor)
        mView.lblDayOfWeek.setTextColor(themeValues.textColor)
        mView.lblMonth.setTextColor(themeValues.textColor)
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
        mView = inflater.inflate(R.layout.fragment_day, container, false)
        setTheme()

        val viewedDate: LocalDate = mainActivity.viewedDate
        val possibleNote: NoteData? = mainActivity.noteRepository.getByDate(viewedDate)

        if (possibleNote == null) {
            mView.btnNoteAdd.isVisible = true
        }
        else {
            mView.btnNoteAdd.isVisible = false
            fragmentSetter.setFragment(
                DayNoteFragment.createInstance(this, possibleNote))
        }

        handleTouchEvent()
        handleBtnNoteAddClickEvent()
        setLabelsText(viewedDate)
        return mView
    }

    companion object : MainFragmentCreator<DayFragment> {
        @JvmStatic
        override fun createInstance(): DayFragment = DayFragment()

        @JvmStatic
        override val fragmentType: MainActivity.MainFragmentType
                = MainActivity.MainFragmentType.DAY_FRAGMENT
    }
}