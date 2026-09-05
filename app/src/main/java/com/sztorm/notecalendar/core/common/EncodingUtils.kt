package com.sztorm.notecalendar.core.common

import android.util.Base64
import java.nio.charset.Charset

data class Base64String(val text: String, val encodingFlags: Int) {
    fun decoded(): ByteArray = Base64.decode(text, encodingFlags)

    override fun toString() = text
}

fun String.toBase64(charset: Charset = Charsets.UTF_8, flags: Int = Base64.DEFAULT) = Base64String(
    text = Base64.encodeToString(toByteArray(charset), flags),
    encodingFlags = flags
)

fun ByteArray.toBase64(flags: Int = Base64.DEFAULT) = Base64String(
    text = Base64.encodeToString(this, flags),
    encodingFlags = flags
)