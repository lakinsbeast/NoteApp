package code.with.me.testroomandnavigationdrawertest.data.data_classes

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FolderTag(
    val name: String,
    val lastTimestampCreate: Long,
    val lastTimestampEdited: Long,
    val lastTimestampOpen: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    ) : Identifiable, Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeLong(lastTimestampCreate)
        dest.writeLong(lastTimestampEdited)
        dest.writeLong(lastTimestampOpen)
        dest.writeLong(id)
    }

    companion object CREATOR : Parcelable.Creator<FolderTag> {
        override fun createFromParcel(parcel: Parcel): FolderTag {
            return FolderTag(
                parcel.readString() ?: "",
                parcel.readLong(),
                parcel.readLong(),
                parcel.readLong(),
                parcel.readLong(),
            )
        }

        override fun newArray(size: Int): Array<FolderTag?> {
            return arrayOfNulls(size)
        }
    }
}