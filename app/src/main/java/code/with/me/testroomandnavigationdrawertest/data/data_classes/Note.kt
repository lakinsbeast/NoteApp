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
    @ColumnInfo(name = "folderId") val folderId: Int
) {
    fun toNewNote(): NewNote {
        return NewNote().apply {
            _id = this@Note.id
            titleNote = this@Note.titleNote
            textNote = this@Note.textNote
            imageById = this@Note.imageById
            audioUrl = this@Note.audioUrl
            paintUrl = this@Note.paintUrl
            imgFrmGlrUrl = this@Note.imgFrmGlrUrl
            colorCard = this@Note.colorCard
            folderId = this@Note.folderId
        }
    }
}

//создал модельку для удобного создания адаптера
@Entity()
class NewNote: Model() {
    @PrimaryKey(autoGenerate = true) var _id: Int = 0
    @ColumnInfo(name = "title") var titleNote: String = ""
    @ColumnInfo(name = "text") var textNote: String = ""
    @ColumnInfo(name = "imgUrl") var imageById: String = ""
    @ColumnInfo(name = "audioUrl") var audioUrl: String = ""
    @ColumnInfo(name = "paintUrl") var paintUrl: String = ""
    @ColumnInfo(name = "imgFrmGlrUrl") var imgFrmGlrUrl: String = ""
    @ColumnInfo(name = "colorCard") var colorCard: String = ""
    @ColumnInfo(name = "folderId") var folderId: Int = 0
}
