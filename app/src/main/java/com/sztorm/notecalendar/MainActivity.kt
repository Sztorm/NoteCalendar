package com.sztorm.notecalendar

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.sztorm.notecalendar.databinding.ActivityMainBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentSetter: FragmentSetter
    private lateinit var currentFragmentType: MainFragmentType
    private var _themePainter: ThemePainter? = null
    private var _settingsIO: SettingsIO? = null
    private val settingsIO: SettingsIO
        get() {
            val result = _settingsIO ?: SettingsIO(this)

            if (_settingsIO == null) {
                _settingsIO = result
            }
            return result
        }
    var viewedDate: LocalDate = LocalDate.now()
    val themePainter: ThemePainter
        get() {
            val result = _themePainter ?: ThemePainter(settingsReader.themeValues)

            if (_themePainter == null) {
                _themePainter = result
            }
            return result
        }
    val settingsReader: SettingsReader
        get() = settingsIO

    private fun setTheme() {
        themePainter.apply {
            paintWindowStatusBar(window)
            paintNavigationButton(binding.btnViewMonth)
            paintNavigationButton(binding.btnViewWeek)
            paintNavigationButton(binding.btnViewDay)
            paintNavigationButton(binding.btnViewSettings)
            paintBackground(binding.root)
        }
    }

    private fun setNavigationButtonClickListener(button: Button, fragmentType: MainFragmentType) =
        button.setOnClickListener {
            if (currentFragmentType != fragmentType) {
                currentFragmentType = fragmentType
                fragmentSetter.setFragment(fragmentType.createFragment())
            }
        }

    private fun setBackButtonPressListener() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentFragmentType == MainFragmentType.CUSTOM_THEME_SETTINGS) {
                    setMainFragment(MainFragmentType.ROOT_SETTINGS)
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setMainFragmentOnCreate() {
        val bundle: Bundle? = intent.extras

        if (bundle === null) {
            setMainFragment(
                settingsIO.getStartingView(StartingViewType.DAY_VIEW).toMainFragmentType()
            )
            return
        }
        val mainFragmentTypeOrdinal: Int = bundle.getInt(
            BUNDLE_KEY_MAIN_FRAGMENT_TYPE, MainFragmentType.DAY.ordinal
        )
        setMainFragment(
            MainFragmentType.from(mainFragmentTypeOrdinal),
            resAnimIn = R.anim.anim_immediate,
            resAnimOut = R.anim.anim_immediate
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settingsIO = SettingsIO(this)
        _settingsIO = settingsIO
        _themePainter = ThemePainter(settingsIO.themeValues)
        fragmentSetter = FragmentSetter(supportFragmentManager, R.id.mainFragmentContainer)
        setTheme()
        setNavigationButtonClickListener(binding.btnViewDay, MainFragmentType.DAY)
        setNavigationButtonClickListener(binding.btnViewWeek, MainFragmentType.WEEK)
        setNavigationButtonClickListener(binding.btnViewMonth, MainFragmentType.MONTH)
        setNavigationButtonClickListener(binding.btnViewSettings, MainFragmentType.ROOT_SETTINGS)
        setBackButtonPressListener()
        setMainFragmentOnCreate()
        if (tryScheduleNoteNotification(ScheduleNoteNotificationArguments())) {
            Timber.i(
                "${LogTags.NOTIFICATIONS} Scheduled notification upon MainActivity creation"
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (settingsReader.enabledNotifications &&
            !AppPermissionManager.areScheduleNotificationPermissionsGrantedOnRequest(
                requestCode, grantResults
            )
        ) {
            settingsIO.enabledNotifications = false
        }
    }

    fun setMainFragment(
        mainFragmentType: MainFragmentType,
        resAnimIn: Int = R.anim.anim_in,
        resAnimOut: Int = R.anim.anim_out
    ) {
        val navigation: MaterialButtonToggleGroup = binding.navigation

        if (navigation.checkedButtonId != MAIN_BUTTON_RESOURCE_IDS[mainFragmentType.ordinal]) {
            navigation.check(MAIN_BUTTON_RESOURCE_IDS[mainFragmentType.ordinal])
        }
        currentFragmentType = mainFragmentType
        fragmentSetter.setFragment(mainFragmentType.createFragment(), resAnimIn, resAnimOut)
    }

    fun tryScheduleNoteNotification(args: ScheduleNoteNotificationArguments): Boolean {
        val enabledNotifications: Boolean =
            args.enabledNotifications ?: settingsReader.enabledNotifications

        if (!enabledNotifications) {
            return false
        }
        val notificationTime: TimePickerPreference.Time =
            args.notificationTime ?: settingsReader.notificationTime
        val currentDateTime = LocalDateTime.now()
        var notificationDateTime = LocalDateTime.of(
            currentDateTime.toLocalDate(), notificationTime.toLocalTime()
        )
        if (notificationTime.toLocalTime() <= currentDateTime.toLocalTime()) {
            notificationDateTime = notificationDateTime.plusDays(1)
        }
        val note: NoteData? =
            args.note ?: NoteRepository.getByDate(notificationDateTime.toLocalDate())

        if (note === null || note.date != notificationDateTime.toLocalDate().toString()) {
            return false
        }
        val notificationData = NoteNotificationData(note, notificationDateTime)

        NoteNotificationManager.scheduleNotification(this, notificationData)
        return true
    }

    fun tryCancelScheduledNotification(noteDate: LocalDate): Boolean {
        val notificationTime: TimePickerPreference.Time = settingsReader.notificationTime
        val currentDateTime = LocalDateTime.now()
        var notificationDateTime = LocalDateTime.of(
            currentDateTime.toLocalDate(), notificationTime.toLocalTime()
        )
        if (notificationTime.toLocalTime() <= currentDateTime.toLocalTime()) {
            notificationDateTime = notificationDateTime.plusDays(1)
        }
        if (notificationDateTime.toLocalDate() != noteDate) {
            return false
        }
        NoteNotificationManager.cancelScheduledNotification(this)

        return true
    }

    fun restart(startingMainFragment: MainFragmentType) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY_MAIN_FRAGMENT_TYPE, startingMainFragment.ordinal)

        val intent = Intent(applicationContext, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            .putExtras(bundle)
        val options = ActivityOptions.makeCustomAnimation(baseContext, 0, 0)

        startActivity(intent, options.toBundle())
        finish()
    }

    companion object {
        private const val BUNDLE_KEY_MAIN_FRAGMENT_TYPE = "MainFragmentType"
        private val MAIN_BUTTON_RESOURCE_IDS: IntArray = intArrayOf(
            R.id.btnViewDay,
            R.id.btnViewWeek,
            R.id.btnViewMonth,
            R.id.btnViewSettings,
            R.id.btnViewSettings
        )
    }
}