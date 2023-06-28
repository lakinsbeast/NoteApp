package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderTagDAO {
    @Query("SELECT * FROM foldertag")
    fun getAllTags(): Flow<List<FolderTag>>


    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertTag(folderTag: FolderTag)
}