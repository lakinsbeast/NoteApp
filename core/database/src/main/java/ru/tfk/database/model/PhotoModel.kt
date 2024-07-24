package ru.tfk.database.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.tfk.model.Identifiable

@Serializable
class PhotoModel(
    val path: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
) : Identifiable, Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) {
        dest.writeString(path)
        dest.writeLong(id)
    }

    companion object CREATOR : Parcelable.Creator<PhotoModel> {
        override fun createFromParcel(parcel: Parcel): PhotoModel {
            val id = parcel.readLong()
            val path = parcel.readString() ?: ""
            return PhotoModel(path, id)
        }

        override fun newArray(size: Int): Array<PhotoModel?> {
            return arrayOfNulls(size)
        }
    }
}
