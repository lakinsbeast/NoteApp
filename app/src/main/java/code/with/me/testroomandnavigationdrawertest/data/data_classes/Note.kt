package code.with.me.testroomandnavigationdrawertest.data.data_classes

import android.view.Display.Mode
import androidx.room.*

@Entity()
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val titleNote: String,
    @ColumnInfo(name = "text") val textNote: String,
    @ColumnInfo(name = "imgUrl") val imageById: String,
    @ColumnInfo(name = "audioUrl") val audioUrl: String,
    @ColumnInfo(name = "paintUrl") val paintUrl: String,
    @ColumnInfo(name = "imgFrmGlrUrl") val imgFrmGlrUrl: String,
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
            imageById,
            audioUrl,
            paintUrl,
            imgFrmGlrUrl,
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
    @ColumnInfo(name = "imgUrl") var imageById: String,
    @ColumnInfo(name = "audioUrl") var audioUrl: String,
    @ColumnInfo(name = "paintUrl") var paintUrl: String,
    @ColumnInfo(name = "imgFrmGlrUrl") var imgFrmGlrUrl: String,
    @ColumnInfo(name = "colorCard") var colorCard: String,
    @ColumnInfo(name = "folderId") var folderId: Int,
    @ColumnInfo(name = "lastTimestampCreate") val lastTimestampCreate: Long,
    @ColumnInfo(name = "lastTimestampOpen") val lastTimestampOpen: Long,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean,
    @ColumnInfo(name = "tags") val tags: String,
) : Model()
