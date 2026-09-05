package com.sztorm.notecalendar.feature.settings.notes

data class ImportNoteBackupPreferenceTexts(
    val title: String,
    val summary: String?,
    val dialogTexts: ImportNoteBackupPreferenceDialogTexts
) {
    companion object {
        fun english() = ImportNoteBackupPreferenceTexts(
            title = "Import notes backup",
            summary = null,
            dialogTexts = ImportNoteBackupPreferenceDialogTexts.english()
        )
    }
}

data class ImportNoteBackupPreferenceDialogTexts(
    val title: String,
    val password: String,
    val incorrectPassword: String,
) {
    companion object {
        fun english() = ImportNoteBackupPreferenceDialogTexts(
            title = "Notes backup import",
            password = "Password",
            incorrectPassword = "Password is incorrect"
        )
    }
}