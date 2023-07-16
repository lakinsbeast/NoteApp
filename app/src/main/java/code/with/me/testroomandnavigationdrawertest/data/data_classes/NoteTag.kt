package code.with.me.testroomandnavigationdrawertest.data.data_classes

import androidx.room.Entity

@Entity
class NoteTag(
    val name: String,
    val lastTimestampCreate: Long,
    val lastTimestampEdited: Long,
    val lastTimestampOpen: Long,
) : Model() {
}