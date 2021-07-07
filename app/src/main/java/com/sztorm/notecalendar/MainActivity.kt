@file:Suppress("MemberVisibilityCanBePrivate")

package com.sztorm.notecalendar

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.orm.SchemaGenerator
import com.orm.SugarContext
import com.orm.SugarDb
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager
import com.sztorm.notecalendar.TimePickerPreference.Time.Companion.asTime
import com.sztorm.notecalendar.helpers.ColorPickerPreferenceManagerHelper.Companion.getThemeColors
import com.sztorm.notecalendar.repositories.NoteRepository
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    companion object {
        private const val BUNDLE_KEY_MAIN_FRAGMENT_TYPE = "MainFragmentType"
    }

    enum class MainFragmentType {
        DAY_FRAGMENT,
        WEEK_FRAGMENT,
        MONTH_FRAGMENT,
        SETTINGS_FRAGMENT;

        companion object {
            private val VALUES: Array<MainFragmentType> = values()

            fun from(ordinal: Int) =
                try { VALUES[ordinal] }
                catch (e: IndexOutOfBoundsException) {
                    throw IllegalArgumentException("Value is out of range of enum ordinals. The " +
                            "value must be in [0, 3] range.")
                }
        }
    }

    private val mainButtonResourceIds: IntArray = intArrayOf(
        R.id.btnViewDay, R.id.btnViewWeek, R.id.btnViewMonth, R.id.btnViewSettings)
    private lateinit var fragmentSetter: FragmentSetter
    private lateinit var currentFragmentType: MainFragmentType
    private lateinit var mThemePainter: ThemePainter
    private var mViewedDate: LocalDate = LocalDate.now()
    val noteRepository = NoteRepository()
    val viewedDate: LocalDate
        get() = mViewedDate
    val themePainter: ThemePainter
        get() = mThemePainter
    
    private fun initDatabase() {
        SugarContext.init(this)
        val schemaGenerator = SchemaGenerator(this)
        schemaGenerator.createDatabase(SugarDb(this).db)
    }

    private fun setTheme() {
        val themeValues: ThemeValues = mThemePainter.values

        mThemePainter.paintWindowStatusBar(window)
        mThemePainter.paintNavigationButton(btnViewMonth)
        mThemePainter.paintNavigationButton(btnViewWeek)
        mThemePainter.paintNavigationButton(btnViewDay)
        mThemePainter.paintNavigationButton(btnViewSettings)
        mainActivityView.setBackgroundColor(themeValues.backgroundColor)
    }

    private fun <T, TCreator> handleNavigationButtonClickEvent(
        button: Button, mainFragmentCreator: TCreator)
        where T : Fragment, TCreator : MainFragmentCreator<T> {

        button.setOnClickListener {
            if (currentFragmentType != mainFragmentCreator.fragmentType) {
                currentFragmentType = mainFragmentCreator.fragmentType
                fragmentSetter.setFragment(mainFragmentCreator)
            }
        }
    }

    private fun setMainFragmentOnCreate() {
        val bundle: Bundle? = intent.extras

        if (bundle === null) {
            setMainFragment(DayFragment)
        }
        else {
            val fragmentTypeOrdinal: Int = bundle.getInt(
                BUNDLE_KEY_MAIN_FRAGMENT_TYPE, MainFragmentType.DAY_FRAGMENT.ordinal)

            setMainFragment(MainFragmentType.from(fragmentTypeOrdinal),
                resAnimIn = R.anim.anim_immediate,
                resAnimOut = R.anim.anim_immediate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val themeValues: ThemeValues = ColorPickerPreferenceManager
            .getInstance(this)
            .getThemeColors(this)
        mThemePainter = ThemePainter(themeValues)
        fragmentSetter = FragmentSetter(supportFragmentManager, R.id.mainFragmentContainer)
        setTheme()
        initDatabase()
        handleNavigationButtonClickEvent(btnViewDay, DayFragment)
        handleNavigationButtonClickEvent(btnViewWeek, WeekFragment)
        handleNavigationButtonClickEvent(btnViewMonth, MonthFragment)
        handleNavigationButtonClickEvent(btnViewSettings, SettingsFragment)
        setMainFragmentOnCreate()
        scheduleNoteNotification(ScheduleNoteNotificationArguments())
    }

    fun <T, TCreator> setMainFragment(
        fragmentCreator: TCreator,
        resAnimIn: Int = R.anim.anim_in,
        resAnimOut: Int = R.anim.anim_out,
        date: LocalDate = mViewedDate)
            where T : Fragment, TCreator : MainFragmentCreator<T> {
        if (date != mViewedDate) {
            mViewedDate = date
        }
        currentFragmentType = fragmentCreator.fragmentType
        navigation.check(mainButtonResourceIds[currentFragmentType.ordinal])
        fragmentSetter.setFragment(fragmentCreator, resAnimIn, resAnimOut)
    }

    fun setMainFragment(
        mainFragmentType: MainFragmentType,
        resAnimIn: Int = R.anim.anim_in,
        resAnimOut: Int = R.anim.anim_out,
        date: LocalDate = mViewedDate) {
        if (date != mViewedDate) {
            mViewedDate = date
        }
        currentFragmentType = mainFragmentType
        navigation.check(mainButtonResourceIds[mainFragmentType.ordinal])

        when (mainFragmentType) {
            DayFragment.fragmentType -> fragmentSetter.setFragment(
                DayFragment, resAnimIn, resAnimOut)
            WeekFragment.fragmentType -> fragmentSetter.setFragment(
                WeekFragment, resAnimIn, resAnimOut)
            MonthFragment.fragmentType -> fragmentSetter.setFragment(
                MonthFragment, resAnimIn, resAnimOut)
            SettingsFragment.fragmentType -> fragmentSetter.setFragment(
                SettingsFragment, resAnimIn, resAnimOut)
            else -> fragmentSetter.setFragment(DayFragment)
        }
    }

    fun scheduleNoteNotification(args: ScheduleNoteNotificationArguments) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val enabledNotifications = args.enabledNotifications ?: preferences.getBoolean(
                getString(R.string.PrefKey_EnableNotifications), false)

        if (!enabledNotifications) {
            return
        }
        val notificationTime = args.notificationTime ?: preferences
            .getInt(getString(R.string.PrefKey_NotificationTime), TimePickerPreference.DEFAULT_TIME_RAW)
            .asTime()
        val currentDateTime = LocalDateTime.now()
        var notificationDateTime = LocalDateTime.of(
            currentDateTime.toLocalDate(), notificationTime.toLocalTime())

        if (notificationTime.toLocalTime() <= currentDateTime.toLocalTime()) {
            notificationDateTime = notificationDateTime.plusDays(1)
        }
        val note: NoteData? = args.note ?: noteRepository.getByDate(notificationDateTime.toLocalDate())

        if (note === null) {
            return
        }
        val notificationData = NoteNotificationData(note, notificationDateTime)

        NoteNotificationManager.scheduleNotification(this, notificationData)
    }

    fun <T, TCreator> restart(startingMainFragment: TCreator)
            where T : Fragment, TCreator : MainFragmentCreator<T> {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY_MAIN_FRAGMENT_TYPE, startingMainFragment.fragmentType.ordinal)

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtras(bundle)

        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}