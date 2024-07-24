package ru.tfk.data.model

import ru.tfk.database.model.Note

data class DataClassAdapter(
    val clickListener: (Int) -> Unit,
    val notesArray: ArrayList<Note>,
)
