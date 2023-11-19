package com.sztorm.notecalendar

interface Arguments

object CreateOrEditNoteRequest : Arguments

class UndoNoteDeleteOption(val note: NoteData) : Arguments