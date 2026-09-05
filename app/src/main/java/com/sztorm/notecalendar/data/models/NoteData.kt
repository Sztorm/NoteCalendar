package com.sztorm.notecalendar.data.models

import com.orm.SugarRecord
import com.orm.dsl.Unique

data class NoteData(
    @Unique val date: String = "",
    val text: String = "",
    val reminderDateTime: String = ""
) : SugarRecord()