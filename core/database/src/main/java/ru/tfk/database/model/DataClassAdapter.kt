package ru.tfk.database.model

data class DataClassAdapter(
    val clickListener: (Int) -> Unit,
    val notesArray: ArrayList<Note>,
)
