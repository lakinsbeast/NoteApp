package ru.tfk.data.repository

import kotlinx.coroutines.flow.Flow
import ru.tfk.database.model.Folder
import ru.tfk.database.model.Note

interface FolderRepository {
    fun getAllFolders(): Flow<List<Folder>>

    fun getNotesInFolder(folderId: Long): Flow<List<Note>>

    fun getAllFoldersSortByNameASC(): Flow<List<Folder>>

    fun getAllFoldersSortByNameDESC(): Flow<List<Folder>>

    fun getAllFoldersLastOpenedNewest(): Flow<List<Folder>>

    fun getAllFoldersLastOpenedOldest(): Flow<List<Folder>>

    fun getAllFoldersLastEditedNewest(): Flow<List<Folder>>

    fun getAllFoldersLastEditedOldest(): Flow<List<Folder>>

    fun updateLastOpenedFolder(
        time: Long,
        folderId: Long,
    )

    suspend fun getAllFoldersFavorite(): Flow<List<Folder>>

    suspend fun insertFolder(folder: Folder): Long

    suspend fun updateFolder(folder: Folder)

    suspend fun deleteFolder(folder: Folder)

    fun getFolderByTag(tag: String): Flow<List<Folder>>
}
