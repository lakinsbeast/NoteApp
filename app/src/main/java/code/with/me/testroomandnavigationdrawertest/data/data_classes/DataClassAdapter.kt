package code.with.me.testroomandnavigationdrawertest.data.data_classes

data class DataClassAdapter(
    val clickListener: (Int) -> Unit,
    val notesArray: ArrayList<Note>
)

class NoteClass: Model() {
    val notesArray: ArrayList<Note> = ArrayList()
}