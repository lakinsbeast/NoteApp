package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteTag
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteTagDAO {
    @Query("SELECT * FROM notetag")
    fun getAllTags(): Flow<List<NoteTag>>

    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertTag(noteTag: NoteTag)
}
