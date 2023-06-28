package code.with.me.testroomandnavigationdrawertest.data.repos

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderTagDAO
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val fDao: FolderDAO,
    private val tDao: FolderTagDAO
) : FolderRepository {
    override fun getAllFolders(): Flow<List<Folder>> = fDao.getAllFolders()

    override fun getNotesInFolder(folderId: Int): Flow<List<Note>> = fDao.getNotesInFolder(folderId)
    override fun getAllFoldersSortByNameASC(): Flow<List<Folder>> =
        fDao.getAllFoldersSortByNameASC()

    override fun getAllFoldersSortByNameDESC(): Flow<List<Folder>> =
        fDao.getAllFoldersSortByNameDESC()

    override fun getAllFoldersLastOpenedNewest(): Flow<List<Folder>> =
        fDao.getAllFoldersLastOpenedNewest()

    override fun getAllFoldersLastOpenedOldest(): Flow<List<Folder>> =
        fDao.getAllFoldersLastOpenedOldest()

    override fun getAllFoldersLastEditedNewest(): Flow<List<Folder>> =
        fDao.getAllFoldersLastEditedNewest()

    override fun getAllFoldersLastEditedOldest(): Flow<List<Folder>> =
        fDao.getAllFoldersLastEditedOldest()

    override fun getAllFoldersFavorite(): Flow<List<Folder>> = fDao.getAllFoldersFavorite()

    override suspend fun insertFolder(folder: Folder): Long = fDao.insertFolder(folder)

    override suspend fun updateFolder(folder: Folder) = fDao.updateFolder(folder)

    override suspend fun deleteFolder(folder: Folder) = fDao.deleteFolder(folder)
}