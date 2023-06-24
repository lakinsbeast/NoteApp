package code.with.me.testroomandnavigationdrawertest.domain.repo

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getAllFolders(): Flow<List<Folder>>
    fun getNotesInFolder(folderId: Int): Flow<List<Note>>
    suspend fun insertFolder(folder: Folder): Long
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)

}