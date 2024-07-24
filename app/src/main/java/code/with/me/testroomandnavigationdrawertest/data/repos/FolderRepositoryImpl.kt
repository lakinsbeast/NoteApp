package code.with.me.testroomandnavigationdrawertest.data.repos

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderTagDAO
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FolderRepositoryImpl
@Inject
constructor(
    private val folderDAO: FolderDAO,
    private val folderTagDAO: FolderTagDAO,
) : FolderRepository {
    override fun getAllFolders(): Flow<List<Folder>> = folderDAO.getAllFolders()

    override fun getNotesInFolder(folderId: Long): Flow<List<Note>> =
        folderDAO.getNotesInFolder(folderId)

    override fun getAllFoldersSortByNameASC(): Flow<List<Folder>> =
        folderDAO.getAllFoldersSortByNameASC()

    override fun getAllFoldersSortByNameDESC(): Flow<List<Folder>> =
        folderDAO.getAllFoldersSortByNameDESC()

    override fun getAllFoldersLastOpenedNewest(): Flow<List<Folder>> =
        folderDAO.getAllFoldersLastOpenedNewest()

    override fun getAllFoldersLastOpenedOldest(): Flow<List<Folder>> =
        folderDAO.getAllFoldersLastOpenedOldest()

    override fun getAllFoldersLastEditedNewest(): Flow<List<Folder>> =
        folderDAO.getAllFoldersLastEditedNewest()

    override fun getAllFoldersLastEditedOldest(): Flow<List<Folder>> =
        folderDAO.getAllFoldersLastEditedOldest()

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
