package code.with.me.testroomandnavigationdrawertest.data.data_classes

import android.view.Display.Mode
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Entity()
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val titleNote: String,
    @ColumnInfo(name = "text") val textNote: String,
    @ColumnInfo(name = "listOfImages") val listOfImages: List<PhotoModel>,
    @ColumnInfo(name = "audioUrl") val audioUrl: String,
    @ColumnInfo(name = "colorCard") val colorCard: String,
    @ColumnInfo(name = "folderId") val folderId: Int,
    @ColumnInfo(name = "lastTimestampCreate") val lastTimestampCreate: Long,
    @ColumnInfo(name = "lastTimestampOpen") val lastTimestampOpen: Long,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean,
    @ColumnInfo(name = "tags") val tags: String,
) {
    fun toNewNote(): NewNote {
        return NewNote(
            id,
            titleNote,
            textNote,
            listOfImages,
            audioUrl,
            colorCard,
            folderId,
            lastTimestampCreate,
            lastTimestampOpen,
            isFavorite,
            tags
        )
    }
}

//создал модельку для удобного создания адаптера
@Entity()
class NewNote(
    @PrimaryKey(autoGenerate = true) var _id: Int,
    @ColumnInfo(name = "title") var titleNote: String,
    @ColumnInfo(name = "text") var textNote: String,
    @Serializable @ColumnInfo(name = "listOfImages") var listOfImages: List<PhotoModel>,
    @ColumnInfo(name = "audioUrl") var audioUrl: String,
    @ColumnInfo(name = "colorCard") var colorCard: String,
    @ColumnInfo(name = "folderId") var folderId: Int,
    @ColumnInfo(name = "lastTimestampCreate") val lastTimestampCreate: Long,
    @ColumnInfo(name = "lastTimestampOpen") val lastTimestampOpen: Long,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean,
    @ColumnInfo(name = "tags") val tags: String,
) : Model()
