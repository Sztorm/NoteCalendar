package com.sztorm.notecalendar

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.repositories.NoteRepository
import com.orm.SchemaGenerator
import com.orm.SugarContext
import com.orm.SugarDb
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private enum class FragmentType {
        OTHER,
        DAY_FRAGMENT,
        WEEK_FRAGMENT,
        MONTH_FRAGMENT
    }

    private lateinit var fragmentSetter: FragmentSetter
    private lateinit var currentFragmentType: FragmentType
    var viewedDate: LocalDate = LocalDate.now()
    val noteRepository = NoteRepository()

    private fun initDatabase() {
        SugarContext.init(this)
        val schemaGenerator = SchemaGenerator(this)
        schemaGenerator.createDatabase(SugarDb(this).db)
    }

    fun <T, TCreator> setFragment(fragmentCreator: TCreator)
            where T : Fragment, TCreator : InstanceCreator<T> {
        when (fragmentCreator) {
            is DayFragment.Companion -> {
                currentFragmentType = FragmentType.DAY_FRAGMENT
                groupNavBtn.check(R.id.btnViewDay)
            }
            is WeekFragment.Companion -> {
                currentFragmentType = FragmentType.WEEK_FRAGMENT
                groupNavBtn.check(R.id.btnViewWeek)
            }
            is MonthFragment.Companion -> {
                currentFragmentType = FragmentType.MONTH_FRAGMENT
                groupNavBtn.check(R.id.btnViewMonth)
            }
        }
        fragmentSetter.setFragment(fragmentCreator)
    }

    private fun handleBtnViewDayClickEvent() = btnViewDay.setOnClickListener {
        if (currentFragmentType != FragmentType.DAY_FRAGMENT) {
            currentFragmentType = FragmentType.DAY_FRAGMENT
            fragmentSetter.setFragment(DayFragment)
        }
    }

    private fun handleBtnViewWeekClickEvent() = btnViewWeek.setOnClickListener {
        if (currentFragmentType != FragmentType.WEEK_FRAGMENT) {
            currentFragmentType = FragmentType.WEEK_FRAGMENT
            fragmentSetter.setFragment(WeekFragment)
        }
    }

    private fun handleBtnViewMonthClickEvent() = btnViewMonth.setOnClickListener {
        if (currentFragmentType != FragmentType.MONTH_FRAGMENT) {
            currentFragmentType = FragmentType.MONTH_FRAGMENT
            fragmentSetter.setFragment(MonthFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDatabase()
        fragmentSetter = FragmentSetter(supportFragmentManager, R.id.mainFragmentContainer)
        currentFragmentType = FragmentType.DAY_FRAGMENT
        groupNavBtn.check(R.id.btnViewDay)
        handleBtnViewDayClickEvent()
        handleBtnViewWeekClickEvent()
        handleBtnViewMonthClickEvent()

        fragmentSetter.setFragment(DayFragment)
    }
}