package com.sztorm.notecalendar.core.logging

interface AppLogger {
    fun error(throwable: Throwable? = null, message: String? = null, vararg args: Any?)

    fun warning(message: String, vararg args: Any?)

    fun info(message: String, vararg args: Any?)

    fun verbose(message: String, vararg args: Any?)
}