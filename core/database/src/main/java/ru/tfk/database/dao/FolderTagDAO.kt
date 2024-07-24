package ru.tfk.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.tfk.database.model.FolderTag

@Dao
interface FolderTagDAO {
    @Query("SELECT * FROM foldertag")
    fun getAllTags(): Flow<List<FolderTag>>

    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertTag(folderTag: FolderTag)
}
