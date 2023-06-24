package code.with.me.testroomandnavigationdrawertest.data.repos

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderDAO
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(private val dao: FolderDAO) : FolderRepository {
    override fun getAllFolders(): Flow<List<Folder>> = dao.getAllFolders()

    override fun getNotesInFolder(folderId: Int): Flow<List<Note>> = dao.getNotesInFolder(folderId)

    override suspend fun insertFolder(folder: Folder): Long = dao.insertFolder(folder)

    override suspend fun updateFolder(folder: Folder) = dao.updateFolder(folder)

    override suspend fun deleteFolder(folder: Folder) = dao.deleteFolder(folder)
}