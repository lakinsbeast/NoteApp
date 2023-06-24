package code.with.me.testroomandnavigationdrawertest.data.data_classes

import androidx.room.Entity
import androidx.room.PrimaryKey
import code.with.me.testroomandnavigationdrawertest.Utils.randomId

@Entity
open class Model {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}