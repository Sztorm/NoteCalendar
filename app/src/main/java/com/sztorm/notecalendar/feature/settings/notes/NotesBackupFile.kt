package com.sztorm.notecalendar.feature.settings.notes

import android.util.Base64
import com.sztorm.notecalendar.core.common.Base64String
import com.sztorm.notecalendar.core.common.EncryptionParameters
import com.sztorm.notecalendar.core.common.EncryptionType
import com.sztorm.notecalendar.core.common.decrypt
import com.sztorm.notecalendar.core.common.encrypt
import com.sztorm.notecalendar.core.common.toBase64
import com.sztorm.notecalendar.core.common.toList
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.core.logging.LogTags
import com.sztorm.notecalendar.domain.models.Note
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

sealed class NotesBackupFile(val version: String) {
    abstract fun toJson(): String

    sealed class V1(val encryptionType: EncryptionType) : NotesBackupFile(version = "1.0") {
        class Plain(val notes: List<Note>) : V1(encryptionType = EncryptionType.None) {
            private fun notesToJson() = notes.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ", "
            ) { (date, text) ->
                val textBase64 = text.toBase64(Charsets.UTF_8, Base64.NO_WRAP)

                """{"date": "$date", "text": "$textBase64" }"""
            }

            override fun toJson(): String =
                StringBuilder().apply {
                    appendLine('{')
                    append("""    "version": """").append(version).appendLine("\",")
                    append("""    "encryptionType": """).append(encryptionType.ordinal)
                        .appendLine(",")
                    append("""    "notes": """).append(notesToJson()).appendLine()
                    appendLine('}')
                }.toString()

            fun encrypted(parameters: EncryptionParameters.Aes) = Aes256Encrypted(
                salt = parameters.salt,
                iv = parameters.iv,
                notes = encrypt(notesToJson().toByteArray(Charsets.UTF_8), parameters)
            )
        }

        class Aes256Encrypted(
            val salt: ByteArray,
            val iv: IvParameterSpec,
            val notes: Base64String
        ) : V1(encryptionType = EncryptionType.Aes256) {
            override fun toJson(): String = StringBuilder().apply {
                appendLine('{')
                append("""    "version": """").append(version).appendLine("\",")
                append("""    "encryptionType": """).append(encryptionType.ordinal)
                    .appendLine(",")
                append("""    "salt": """").append(salt.toBase64(Base64.NO_WRAP))
                    .appendLine("\",")
                append("""    "iv": """").append(iv.iv.toBase64(Base64.NO_WRAP))
                    .appendLine("\",")
                append("""    "notes": """").append(notes).appendLine("\"")
                appendLine('}')
            }.toString()

            fun decrypted(secretKey: SecretKey, logger: AppLogger? = null): Plain? {
                val parameters = EncryptionParameters.Aes(
                    salt = salt,
                    iv = iv,
                    key = secretKey
                )
                decrypt(notes, parameters).onSuccess { notesJsonInBytes ->
                    runCatching {
                        JSONArray(notesJsonInBytes.toString(Charsets.UTF_8))
                            .toList()
                            .mapNotNull {
                                val note = it as? JSONObject

                                if (note == null) {
                                    null
                                } else {
                                    val date = note.getString("date")
                                    val text = note.getString("text").let { textBase64 ->
                                        Base64
                                            .decode(textBase64, Base64.NO_WRAP)
                                            .toString(Charsets.UTF_8)
                                    }
                                    Note(date = LocalDate.parse(date), text = text)
                                }
                            }
                    }.onSuccess {
                        logger?.info("${LogTags.CRYPTO} Notes decryption succeeded.")
                        return Plain(it)
                    }
                    logger?.error(message = "${LogTags.FILE_IO} Decrypted notes wrong JSON format.")
                    return null
                }
                logger?.error(message = "${LogTags.CRYPTO} Notes decryption failed.")
                return null
            }
        }
    }

    companion object {
        fun fromJson(json: String): NotesBackupFile? = try {
            val jsonObject = JSONObject(json)
            val version = jsonObject.getString("version")

            when (version) {
                "1.0" -> {
                    val encryptionTypeInt = jsonObject.getInt("encryptionType")

                    when (EncryptionType.entries.getOrNull(encryptionTypeInt)) {
                        EncryptionType.None -> {
                            val notes = jsonObject
                                .getJSONArray("notes")
                                .toList()
                                .mapNotNull {
                                    val note = it as? JSONObject

                                    if (note == null) {
                                        null
                                    } else {
                                        val date = note.getString("date")
                                        val text = Base64String(
                                            text = note.getString("text"),
                                            encodingFlags = Base64.NO_WRAP
                                        ).decoded()
                                            .toString(Charsets.UTF_8)

                                        Note(date = LocalDate.parse(date), text = text)
                                    }
                                }
                            V1.Plain(notes = notes)
                        }

                        EncryptionType.Aes256 -> {
                            val salt = jsonObject.getString("salt")
                            val iv = jsonObject.getString("iv")
                            val notes = jsonObject.getString("notes")

                            V1.Aes256Encrypted(
                                salt = Base64String(salt, Base64.NO_WRAP)
                                    .decoded(),
                                iv = IvParameterSpec(
                                    Base64String(iv, Base64.NO_WRAP)
                                        .decoded()
                                ),
                                notes = Base64String(notes, Base64.NO_WRAP)
                            )
                        }

                        else -> null
                    }
                }

                else -> null
            }
        } catch (_: JSONException) {
            null
        }
    }
}