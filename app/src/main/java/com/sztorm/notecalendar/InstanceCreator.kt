package com.sztorm.notecalendar

interface InstanceCreator<out TOut> {
    fun createInstance(): TOut
}