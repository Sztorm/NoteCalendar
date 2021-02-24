package com.sztorm.notecalendar

import com.orm.SugarRecord

data class NoteData(
    var date: String = "",
    var text: String = "") : SugarRecord()
