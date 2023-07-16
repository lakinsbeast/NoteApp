package code.with.me.testroomandnavigationdrawertest.domain.repo

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getAllFolders(): Flow<List<Folder>>
    fun getNotesInFolder(folderId: Int): Flow<List<Note>>
    fun getAllFoldersSortByNameASC(): Flow<List<Folder>>
    fun getAllFoldersSortByNameDESC(): Flow<List<Folder>>
    fun getAllFoldersLastOpenedNewest(): Flow<List<Folder>>
    fun getAllFoldersLastOpenedOldest(): Flow<List<Folder>>
    fun getAllFoldersLastEditedNewest(): Flow<List<Folder>>
    fun getAllFoldersLastEditedOldest(): Flow<List<Folder>>

    fun updateLastOpenedFolder(time: Long, folderId: Int)
    suspend fun getAllFoldersFavorite(): Flow<List<Folder>>
    suspend fun insertFolder(folder: Folder): Long
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)

    fun getFolderByTag(tag: String): Flow<List<Folder>>
}