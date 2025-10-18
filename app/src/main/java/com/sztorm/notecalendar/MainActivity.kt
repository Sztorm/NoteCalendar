package com.sztorm.notecalendar

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButtonToggleGroup
import com.sztorm.notecalendar.NoteCalendarApplication.Companion.BUNDLE_KEY_MAIN_FRAGMENT_TYPE
import com.sztorm.notecalendar.databinding.ActivityMainBinding
import com.sztorm.notecalendar.repositories.NoteRepositoryImpl
import com.sztorm.notecalendar.repositories.UserPreferencesRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
        private set
    private lateinit var fragmentSetter: FragmentSetter
    private lateinit var currentFragmentType: MainFragmentType
    private var _settings: UserPreferencesRepository? = null
    private var _permissionManager: AppPermissionManager? = null
    private var _notificationManager: AppNotificationManager? = null
    private var _themePainter: ThemePainter? = null
    val sharedData = AppSharedData(viewedDate = LocalDate.now())
    val settings: UserPreferencesRepository
        get() = _settings!!
    val permissionManager: AppPermissionManager
        get() = _permissionManager!!
    val notificationManager: AppNotificationManager
        get() = _notificationManager!!
    val themePainter: ThemePainter
        get() = _themePainter!!

    private fun setTheme() {
        themePainter.apply {
            paintStatusBarAndSetSystemInsets(
                window,
                binding.navigation,
                binding.mainFragmentContainer
            )
            paintNavigationButton(binding.btnViewMonth)
            paintNavigationButton(binding.btnViewWeek)
            paintNavigationButton(binding.btnViewDay)
            paintNavigationButton(binding.btnViewSettings2)
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
            lifecycleScope.launch {
                setMainFragment(
                    settings.getStartingView(StartingViewType.DAY_VIEW).toMainFragmentType()
                )
            }
            return
        }
        val mainFragmentTypeOrdinal: Int = bundle.getInt(
            BUNDLE_KEY_MAIN_FRAGMENT_TYPE, MainFragmentType.DAY.ordinal
        )
        setMainFragment(
            MainFragmentType.entries[mainFragmentTypeOrdinal],
            resAnimIn = R.anim.anim_immediate,
            resAnimOut = R.anim.anim_immediate
        )
    }

    fun initManagers() {
        _settings = _settings ?: UserPreferencesRepository(this)
        _permissionManager = _permissionManager ?: AppPermissionManager(this)
        _notificationManager = _notificationManager ?: AppNotificationManager(this)

        runBlocking {
            _themePainter = _themePainter ?: ThemePainter(settings.getThemeColors())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fragmentSetter = FragmentSetter(supportFragmentManager, R.id.mainFragmentContainer)
        setContentView(binding.root)
        initManagers()
        setTheme()
        setNavigationButtonClickListener(binding.btnViewDay, MainFragmentType.DAY)
        setNavigationButtonClickListener(binding.btnViewWeek, MainFragmentType.WEEK)
        setNavigationButtonClickListener(binding.btnViewMonth, MainFragmentType.MONTH)
        setNavigationButtonClickListener(binding.btnViewSettings2, MainFragmentType.ROOT_SETTINGS2)
        setNavigationButtonClickListener(binding.btnViewSettings, MainFragmentType.ROOT_SETTINGS)
        setBackButtonPressListener()
        setMainFragmentOnCreate()

        lifecycleScope.launch {
            if (notificationManager.tryScheduleNotification(
                    args = ScheduleNoteNotificationArguments(),
                    noteRepository = NoteRepositoryImpl
                )) {
                Timber.i(
                    "${LogTags.NOTIFICATIONS} Scheduled notification upon MainActivity creation"
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setMainFragment(
        mainFragmentType: MainFragmentType,
        resAnimIn: Int = R.anim.anim_in,
        resAnimOut: Int = R.anim.anim_out,
        args: Arguments? = null
    ) {
        val navigation: MaterialButtonToggleGroup = binding.navigation

        if (navigation.checkedButtonId != MAIN_BUTTON_RESOURCE_IDS[mainFragmentType.ordinal]) {
            navigation.check(MAIN_BUTTON_RESOURCE_IDS[mainFragmentType.ordinal])
        }
        currentFragmentType = mainFragmentType
        fragmentSetter.setFragment(mainFragmentType.createFragment(args), resAnimIn, resAnimOut)
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
        private val MAIN_BUTTON_RESOURCE_IDS: IntArray = intArrayOf(
            R.id.btnViewDay,
            R.id.btnViewWeek,
            R.id.btnViewMonth,
            R.id.btnViewSettings2,
            R.id.btnViewSettings,
            R.id.btnViewSettings
        )
    }
}