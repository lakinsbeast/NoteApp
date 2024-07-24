package ru.tfk.database.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.tfk.model.Identifiable

@Entity
class Folder(
    val name: String,
    val lastTimestampCreate: Long,
    val lastTimestampEdit: Long,
    val lastTimestampOpen: Long,
    val tags: String,
    val isFavorite: Boolean,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
) : Identifiable, Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) {
        dest.writeString(name)
        dest.writeLong(lastTimestampCreate)
        dest.writeLong(lastTimestampEdit)
        dest.writeLong(lastTimestampOpen)
        dest.writeString(tags)
        dest.writeInt(if (isFavorite) 1 else 0)
        dest.writeLong(id)
    }

    companion object CREATOR : Parcelable.Creator<Folder> {
        override fun createFromParcel(parcel: Parcel): Folder {
            return Folder(
                parcel.readString() ?: "",
                parcel.readLong(),
                parcel.readLong(),
                parcel.readLong(),
                parcel.readString() ?: "",
                parcel.readInt() == 1,
                parcel.readLong(),
            )
        }

        override fun newArray(size: Int): Array<Folder?> {
            return arrayOfNulls(size)
        }
    }
}
