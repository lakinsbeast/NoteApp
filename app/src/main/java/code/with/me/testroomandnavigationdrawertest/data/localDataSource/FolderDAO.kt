package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDAO {
    @Query("SELECT * FROM folder")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM note WHERE folderId = :folderId")
    fun getNotesInFolder(folderId: Int): Flow<List<Note>>

    @Query("SELECT * FROM folder ORDER BY name ASC")
    fun getAllFoldersSortByNameASC(): Flow<List<Folder>>

    @Query("SELECT * FROM folder ORDER BY name DESC")
    fun getAllFoldersSortByNameDESC(): Flow<List<Folder>>

    @Query("SELECT * FROM folder ORDER BY lastTimestampOpen ASC")
    fun getAllFoldersLastOpenedNewest(): Flow<List<Folder>>

    @Query("SELECT * FROM folder ORDER BY lastTimestampOpen DESC")
    fun getAllFoldersLastOpenedOldest(): Flow<List<Folder>>

    @Query("SELECT * FROM folder ORDER BY lastTimestampEdit ASC")
    fun getAllFoldersLastEditedNewest(): Flow<List<Folder>>

    @Query("SELECT * FROM folder ORDER BY lastTimestampEdit DESC")
    fun getAllFoldersLastEditedOldest(): Flow<List<Folder>>

    @Query("SELECT * FROM folder WHERE isFavorite = 1 ORDER BY name ASC")
    fun getAllFoldersFavorite(): Flow<List<Folder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder): Long

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)
}