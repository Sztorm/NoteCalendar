@file:Suppress("MemberVisibilityCanBePrivate")

package com.sztorm.notecalendar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.orm.SchemaGenerator
import com.orm.SugarContext
import com.orm.SugarDb
import com.sztorm.notecalendar.databinding.ActivityMainBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    companion object {
        private const val BUNDLE_KEY_MAIN_FRAGMENT_TYPE = "MainFragmentType"
        private const val BUNDLE_KEY_SETTINGS_FRAGMENT_TYPE = "SettingsFragmentType"
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
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentSetter: FragmentSetter
    private lateinit var currentFragmentType: MainFragmentType
    private var mThemePainter: ThemePainter? = null
    private var mSettingsReader: SettingsReader? = null
    private var mViewedDate: LocalDate = LocalDate.now()
    val viewedDate: LocalDate
        get() = mViewedDate
    val themePainter: ThemePainter
        get() = mThemePainter ?: ThemePainter(settingsReader.themeValues)
    val settingsReader: SettingsReader
        get() = mSettingsReader ?: SettingsReader(this)

    private fun setTheme() {
        val themePainter: ThemePainter = themePainter
        val themeValues: ThemeValues = themePainter.values

        themePainter.paintWindowStatusBar(window)
        themePainter.paintNavigationButton(binding.btnViewMonth)
        themePainter.paintNavigationButton(binding.btnViewWeek)
        themePainter.paintNavigationButton(binding.btnViewDay)
        themePainter.paintNavigationButton(binding.btnViewSettings)
        binding.root.setBackgroundColor(themeValues.backgroundColor)
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
            return
        }
        val mainFragmentTypeOrdinal: Int = bundle.getInt(
            BUNDLE_KEY_MAIN_FRAGMENT_TYPE, MainFragmentType.DAY_FRAGMENT.ordinal)

        if (mainFragmentTypeOrdinal == MainFragmentType.SETTINGS_FRAGMENT.ordinal) {
            val settingsFragmentTypeOrdinal: Int = bundle.getInt(
                BUNDLE_KEY_SETTINGS_FRAGMENT_TYPE, SettingsFragment.SettingsFragmentType.ROOT.ordinal)

            setMainFragment(MainFragmentType.SETTINGS_FRAGMENT,
                SettingsFragment.SettingsFragmentType.from(settingsFragmentTypeOrdinal),
                resAnimIn = R.anim.anim_immediate,
                resAnimOut = R.anim.anim_immediate)
            return
        }
        setMainFragment(MainFragmentType.from(mainFragmentTypeOrdinal),
            resAnimIn = R.anim.anim_immediate,
            resAnimOut = R.anim.anim_immediate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSettingsReader = SettingsReader(this)
        mThemePainter = ThemePainter(mSettingsReader!!.themeValues)
        fragmentSetter = FragmentSetter(supportFragmentManager, R.id.mainFragmentContainer)
        setTheme()
        handleNavigationButtonClickEvent(binding.btnViewDay, DayFragment)
        handleNavigationButtonClickEvent(binding.btnViewWeek, WeekFragment)
        handleNavigationButtonClickEvent(binding.btnViewMonth, MonthFragment)
        handleNavigationButtonClickEvent(binding.btnViewSettings, SettingsFragment)
        setMainFragmentOnCreate()
        tryScheduleNoteNotification(ScheduleNoteNotificationArguments())
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
        binding.navigation.check(mainButtonResourceIds[currentFragmentType.ordinal])
        fragmentSetter.setFragment(fragmentCreator, resAnimIn, resAnimOut)
    }

    fun setMainFragment(
        mainFragmentType: MainFragmentType,
        settingsFragmentType: SettingsFragment.SettingsFragmentType
            = SettingsFragment.SettingsFragmentType.ROOT,
        resAnimIn: Int = R.anim.anim_in,
        resAnimOut: Int = R.anim.anim_out,
        date: LocalDate = mViewedDate) {
        if (date != mViewedDate) {
            mViewedDate = date
        }
        currentFragmentType = mainFragmentType
        binding.navigation.check(mainButtonResourceIds[mainFragmentType.ordinal])

        when (mainFragmentType) {
            DayFragment.fragmentType -> fragmentSetter.setFragment(
                DayFragment, resAnimIn, resAnimOut)
            WeekFragment.fragmentType -> fragmentSetter.setFragment(
                WeekFragment, resAnimIn, resAnimOut)
            MonthFragment.fragmentType -> fragmentSetter.setFragment(
                MonthFragment, resAnimIn, resAnimOut)
            SettingsFragment.fragmentType -> fragmentSetter.setFragment(
                SettingsFragment.createInstance(settingsFragmentType), resAnimIn, resAnimOut)
            else -> fragmentSetter.setFragment(DayFragment)
        }
    }

    fun tryScheduleNoteNotification(args: ScheduleNoteNotificationArguments) {
        val enabledNotifications: Boolean = args.enabledNotifications ?:
            settingsReader.enabledNotifications

        if (!enabledNotifications) {
            return
        }
        val notificationTime: TimePickerPreference.Time = args.notificationTime ?:
            settingsReader.notificationTime
        val currentDateTime = LocalDateTime.now()
        var notificationDateTime = LocalDateTime.of(
            currentDateTime.toLocalDate(), notificationTime.toLocalTime())

        if (notificationTime.toLocalTime() <= currentDateTime.toLocalTime()) {
            notificationDateTime = notificationDateTime.plusDays(1)
        }
        val note: NoteData? = args.note ?:
            NoteRepository.getByDate(notificationDateTime.toLocalDate())

        if (note === null) {
            return
        }
        if (note.date != notificationDateTime.toLocalDate().toString()) {
            return
        }
        val notificationData = NoteNotificationData(note, notificationDateTime)

        NoteNotificationManager.scheduleNotification(this, notificationData)
    }

    fun tryCancelScheduledNotification(noteDate: LocalDate) {
        val notificationTime: TimePickerPreference.Time = settingsReader.notificationTime
        val currentDateTime = LocalDateTime.now()
        var notificationDateTime = LocalDateTime.of(
            currentDateTime.toLocalDate(), notificationTime.toLocalTime())

        if (notificationTime.toLocalTime() <= currentDateTime.toLocalTime()) {
            notificationDateTime = notificationDateTime.plusDays(1)
        }
        if (notificationDateTime.toLocalDate() != noteDate) {
            return
        }
        NoteNotificationManager.cancelScheduledNotification(this)
    }

    private fun restart(bundle: Bundle) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtras(bundle)

        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun <T, TCreator> restart(startingMainFragment: TCreator)
            where T : Fragment, TCreator : MainFragmentCreator<T> {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY_MAIN_FRAGMENT_TYPE, startingMainFragment.fragmentType.ordinal)

        restart(bundle)
    }

    fun <T, TCreator> restart(startingSettingsFragment: TCreator)
            where T : Fragment, TCreator : SettingsFragmentCreator<T> {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY_MAIN_FRAGMENT_TYPE, MainFragmentType.SETTINGS_FRAGMENT.ordinal)
        bundle.putInt(
            BUNDLE_KEY_SETTINGS_FRAGMENT_TYPE, startingSettingsFragment.fragmentType.ordinal)

        restart(bundle)
    }
}