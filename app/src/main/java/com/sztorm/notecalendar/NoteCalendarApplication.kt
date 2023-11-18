package com.sztorm.notecalendar

import android.app.Application
import com.orm.SchemaGenerator
import com.orm.SugarContext
import com.orm.SugarDb
import timber.log.Timber

class NoteCalendarApplication : Application() {
    private fun initDebugLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initDatabase() {
        SugarContext.init(this)

        val schemaGenerator = SchemaGenerator(this)
        schemaGenerator.createDatabase(SugarDb(this).db)
    }

    override fun onCreate() {
        super.onCreate()
        initDebugLogger()
        initDatabase()
    }

    override fun onTerminate() {
        SugarContext.terminate()
        super.onTerminate()
    }

    companion object {
        const val BUNDLE_KEY_MAIN_FRAGMENT_TYPE = "MainFragmentType"
    }
}
