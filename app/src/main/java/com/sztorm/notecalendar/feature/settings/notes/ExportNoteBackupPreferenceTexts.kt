package com.sztorm.notecalendar.feature.settings.notes

import com.sztorm.notecalendar.ui.components.PasswordStrengthTexts

data class ExportNoteBackupPreferenceTexts(
    val title: String,
    val summary: String?,
    val dialogTexts: ExportNoteBackupPreferenceDialogTexts,
    val passwordStrengthTexts: PasswordStrengthTexts
) {
    companion object {
        fun english() = ExportNoteBackupPreferenceTexts(
            title = "Export notes backup",
            summary = null,
            dialogTexts = ExportNoteBackupPreferenceDialogTexts.english(),
            passwordStrengthTexts = PasswordStrengthTexts.english()
        )
    }
}

data class ExportNoteBackupPreferenceDialogTexts(
    val title: String,
    val encryptData: String,
    val encryptionAlgorithm: String,
    val password: String,
) {
    companion object {
        fun english() = ExportNoteBackupPreferenceDialogTexts(
            title = "Notes backup export",
            encryptData = "Encrypt data",
            encryptionAlgorithm = "Encryption algorithm",
            password = "Password",
        )
    }
}