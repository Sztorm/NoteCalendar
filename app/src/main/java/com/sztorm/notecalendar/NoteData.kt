package com.sztorm.notecalendar

import com.orm.SugarRecord
import com.orm.dsl.Unique

data class NoteData(
    @Unique val date: String = "",
    val text: String = ""
) : SugarRecord()
