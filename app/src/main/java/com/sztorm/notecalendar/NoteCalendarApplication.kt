package com.sztorm.notecalendar

import android.app.Application
import com.orm.SchemaGenerator
import com.orm.SugarContext
import com.orm.SugarDb
import timber.log.Timber

class NoteCalendarApplication : Application() {
    private fun initDatabase() {
        SugarContext.init(this)

        val schemaGenerator = SchemaGenerator(this)
        schemaGenerator.createDatabase(SugarDb(this).db)
    }

    private fun initDebugLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onCreate() {
        super.onCreate()
        initDatabase()
        initDebugLogger()
    }

    override fun onTerminate() {
        SugarContext.terminate()
        super.onTerminate()
    }
}