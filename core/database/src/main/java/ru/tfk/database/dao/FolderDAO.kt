package ru.tfk.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.tfk.database.model.Folder
import ru.tfk.database.model.Note

@Dao
interface FolderDAO {
    @Query("SELECT * FROM folder")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM note WHERE folderId = :folderId")
    fun getNotesInFolder(folderId: Long): Flow<List<Note>>

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

    @Query("UPDATE folder SET lastTimestampOpen = :time WHERE id = :folderId")
    fun updateLastOpenedFolder(
        time: Long,
        folderId: Long,
    )

    @Query("SELECT * FROM folder WHERE tags LIKE :tag")
    fun getFolderByTag(tag: String): Flow<List<Folder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder): Long

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)
}
