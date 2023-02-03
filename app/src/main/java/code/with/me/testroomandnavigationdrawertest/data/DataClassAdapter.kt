package code.with.me.testroomandnavigationdrawertest.data

import code.with.me.testroomandnavigationdrawertest.Note

data class DataClassAdapter(
    val clickListener: (Int) -> Unit,
    val notesArray: ArrayList<Note>
)
