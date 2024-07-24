package ru.tfk.data.repository

import kotlinx.coroutines.flow.Flow
import ru.tfk.database.dao.FolderDAO
import ru.tfk.database.dao.FolderTagDAO
import ru.tfk.database.model.Folder
import ru.tfk.database.model.Note
import javax.inject.Inject

class FolderRepositoryImpl
    @Inject
    constructor(
        private val folderDAO: FolderDAO,
        private val folderTagDAO: FolderTagDAO,
    ) : FolderRepository {
        override fun getAllFolders(): Flow<List<Folder>> = folderDAO.getAllFolders()

        override fun getNotesInFolder(folderId: Long): Flow<List<Note>> = folderDAO.getNotesInFolder(folderId)

        override fun getAllFoldersSortByNameASC(): Flow<List<Folder>> = folderDAO.getAllFoldersSortByNameASC()

        override fun getAllFoldersSortByNameDESC(): Flow<List<Folder>> = folderDAO.getAllFoldersSortByNameDESC()

        override fun getAllFoldersLastOpenedNewest(): Flow<List<Folder>> = folderDAO.getAllFoldersLastOpenedNewest()

        override fun getAllFoldersLastOpenedOldest(): Flow<List<Folder>> = folderDAO.getAllFoldersLastOpenedOldest()

        override fun getAllFoldersLastEditedNewest(): Flow<List<Folder>> = folderDAO.getAllFoldersLastEditedNewest()

        override fun getAllFoldersLastEditedOldest(): Flow<List<Folder>> = folderDAO.getAllFoldersLastEditedOldest()

        override fun updateLastOpenedFolder(
            time: Long,
            folderId: Long,
        ) = folderDAO.updateLastOpenedFolder(time, folderId)

        override suspend fun getAllFoldersFavorite(): Flow<List<Folder>> = folderDAO.getAllFoldersFavorite()

        override fun getFolderByTag(tag: String): Flow<List<Folder>> = folderDAO.getFolderByTag(tag)

        override suspend fun insertFolder(folder: Folder): Long = folderDAO.insertFolder(folder)

        override suspend fun updateFolder(folder: Folder) = folderDAO.updateFolder(folder)

        override suspend fun deleteFolder(folder: Folder) = folderDAO.deleteFolder(folder)
    }
