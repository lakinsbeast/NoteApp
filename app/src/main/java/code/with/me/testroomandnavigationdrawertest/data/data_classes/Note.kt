package code.with.me.testroomandnavigationdrawertest.data.data_classes

import androidx.room.*
import kotlinx.serialization.Serializable

//создал модельку для удобного создания адаптера recyclerview
@Entity()
class Note(
    @PrimaryKey(autoGenerate = true) var second_id: Int,
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
) : Model() {
    override fun toString(): String {
        return "id: $id \n second_id $second_id \n titleNote $titleNote \n textNote $textNote \n listOfImages $listOfImages \n" +
                " audioUrl $audioUrl \n" +
                " colorCard $colorCard \n" +
                " folderId $folderId \n" +
                " lastTimestampCreate $lastTimestampCreate \n" +
                " lastTimestampOpen $lastTimestampOpen \n" +
                " isFavorite $isFavorite \n" +
                " tags $tags \n"
    }
}
