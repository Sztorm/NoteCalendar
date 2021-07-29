package com.sztorm.notecalendar

import android.app.Application
import com.orm.SchemaGenerator
import com.orm.SugarContext
import com.orm.SugarDb

class NoteCalendarApplication : Application() {
    private fun initDatabase() {
        SugarContext.init(this)

        val schemaGenerator = SchemaGenerator(this)
        schemaGenerator.createDatabase(SugarDb(this).db)
    }

    override fun onCreate() {
        super.onCreate()
        initDatabase()
    }

    override fun onTerminate() {
        SugarContext.terminate()
        super.onTerminate()
    }
}