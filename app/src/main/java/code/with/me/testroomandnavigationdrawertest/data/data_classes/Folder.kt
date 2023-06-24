package code.with.me.testroomandnavigationdrawertest.data.data_classes

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
class Folder(
    val name: String
) : Model()
