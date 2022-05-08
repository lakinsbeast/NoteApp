package code.with.me.testroomandnavigationdrawertest

import android.graphics.Path
import androidx.room.*

@Entity
data class Note (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val titleNote: String,
    @ColumnInfo(name = "text") val textNote: String,
    @ColumnInfo(name = "imgUrl") val imageById: String,
    @ColumnInfo(name = "audioUrl") val audioUrl: String,
    @ColumnInfo(name = "paintUrl") val paintUrl: String
    )
