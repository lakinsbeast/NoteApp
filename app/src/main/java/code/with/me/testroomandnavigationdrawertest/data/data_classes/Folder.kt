package code.with.me.testroomandnavigationdrawertest.data.data_classes

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
class Folder(
    val name: String,
    val lastTimestampCreate: Long,
    val lastTimestampEdit: Long,
    val lastTimestampOpen: Long,
    val tagId: Int,
    val isFavorite: Boolean
) : Model()
