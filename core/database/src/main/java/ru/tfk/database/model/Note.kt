package ru.tfk.database.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import kotlinx.serialization.Serializable
import ru.tfk.model.Identifiable
import ru.tfk.utils.isAndroidVersionGreaterOrEqual

// создал интерфейс, так как нельзя использовать generic like "where T: Note, T: NoteFTS для NoteCard", из-за того, что можно использовать только один класс и бесконечность интерфейсов (правила те же, что для наследования)
interface NoteInterface {
    var id: Long
    val titleNote: String
    val textNote: String
}

// создал модельку для удобного создания адаптера recyclerview
@Entity
class Note(
    @ColumnInfo(name = "title") override var titleNote: String,
    @ColumnInfo(name = "text") override var textNote: String,
    @Serializable @ColumnInfo(name = "listOfImages") var listOfImages: List<PhotoModel>,
    @ColumnInfo(name = "audioUrl") var audioUrl: String,
    @ColumnInfo(name = "colorCard") var colorCard: String,
    @ColumnInfo(name = "folderId") var folderId: Long,
    @ColumnInfo(name = "lastTimestampCreate") val lastTimestampCreate: Long,
    @ColumnInfo(name = "lastTimestampOpen") val lastTimestampOpen: Long,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean,
    @ColumnInfo(name = "tags") val tags: String,
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,
) : Identifiable, Parcelable, NoteInterface {
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

fun Note.toNoteFTS() =
    NoteFTS(
        id = id,
        titleNote = titleNote,
        textNote = textNote,
    )

@Entity
@Fts4(contentEntity = Note::class)
class NoteFTS(
    @ColumnInfo(name = "title") override var titleNote: String,
    @ColumnInfo(name = "text") override var textNote: String,
    @ColumnInfo(name = "id") override var id: Long,
) : Identifiable, Parcelable, NoteInterface {
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
