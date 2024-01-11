package code.with.me.testroomandnavigationdrawertest.data.data_classes

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import code.with.me.testroomandnavigationdrawertest.data.Utils.isAndroidVersionGreaterOrEqual
import kotlinx.serialization.Serializable

// создал модельку для удобного создания адаптера recyclerview
@Entity
class Note(
    @ColumnInfo(name = "title") var titleNote: String,
    @ColumnInfo(name = "text") var textNote: String,
    @Serializable @ColumnInfo(name = "listOfImages") var listOfImages: List<PhotoModel>,
    @ColumnInfo(name = "audioUrl") var audioUrl: String,
    @ColumnInfo(name = "colorCard") var colorCard: String,
    @ColumnInfo(name = "folderId") var folderId: Long,
    @ColumnInfo(name = "lastTimestampCreate") val lastTimestampCreate: Long,
    @ColumnInfo(name = "lastTimestampOpen") val lastTimestampOpen: Long,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean,
    @ColumnInfo(name = "tags") val tags: String,
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
        dest.writeString(titleNote)
        dest.writeString(textNote)
        dest.writeList(listOfImages)
        dest.writeString(audioUrl)
        dest.writeString(colorCard)
        dest.writeLong(folderId)
        dest.writeLong(lastTimestampCreate)
        dest.writeLong(lastTimestampOpen)
        dest.writeLong(if (isFavorite) 1L else 0L)
        dest.writeString(tags)
        dest.writeLong(id)
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                if (isAndroidVersionGreaterOrEqual(Build.VERSION_CODES.TIRAMISU)) {
                    arrayListOf<PhotoModel>().apply {
                        parcel.readList(
                            this,
                            PhotoModel::class.java.classLoader,
                            PhotoModel::class.java,
                        )
                    }
                } else {
                    arrayListOf<PhotoModel>().apply {
                        parcel.readList(
                            this,
                            PhotoModel::class.java.classLoader,
                        )
                    }
                },
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readLong(),
                parcel.readLong(),
                parcel.readLong(),
                parcel.readInt() == 1,
                parcel.readString() ?: "",
                parcel.readLong(),
            )
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity
@Fts4(contentEntity = Note::class)
class NoteFTS(
    @ColumnInfo(name = "title") var titleNote: String,
    @ColumnInfo(name = "text") var textNote: String,
    @ColumnInfo(name = "id") var id: Long,
) : Identifiable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) {
        dest.writeLong(id)
        dest.writeString(titleNote)
        dest.writeString(textNote)
    }

    companion object CREATOR : Parcelable.Creator<NoteFTS> {
        override fun createFromParcel(parcel: Parcel): NoteFTS {
            return NoteFTS(parcel)
        }

        override fun newArray(size: Int): Array<NoteFTS?> {
            return arrayOfNulls(size)
        }
    }
}
