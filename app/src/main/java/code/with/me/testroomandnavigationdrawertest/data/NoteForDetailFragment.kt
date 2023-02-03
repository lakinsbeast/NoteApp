package code.with.me.testroomandnavigationdrawertest.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NoteForDetailFragment(
    val id: Int = 0,
    val titleNote: String = "",
    val textNote: String = "",
    val imageById: String = "",
    val audioUrl: String = "",
    val paintUrl: String = "",
    val imgFrmGlrUrl: String = "",
    val colorCard: String = ""
)
