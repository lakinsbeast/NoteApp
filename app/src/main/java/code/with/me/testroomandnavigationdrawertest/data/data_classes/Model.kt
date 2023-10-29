package code.with.me.testroomandnavigationdrawertest.data.data_classes

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import code.with.me.testroomandnavigationdrawertest.data.Utils.randomId

//@Entity
//open class Model() : Parcelable {
//    @PrimaryKey(autoGenerate = true)
//    var id: Long = 0
//
//    constructor(parcel: Parcel) : this() {
//        id = parcel.readLong()
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeLong(id)
//    }
//
//    companion object CREATOR : Parcelable.Creator<Model> {
//        override fun createFromParcel(parcel: Parcel): Model {
//            return Model(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Model?> {
//            return arrayOfNulls(size)
//        }
//    }
//}

interface Identifiable {

}