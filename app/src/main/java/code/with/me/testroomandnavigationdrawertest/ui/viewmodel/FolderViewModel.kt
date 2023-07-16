package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import java.lang.IllegalArgumentException
import javax.inject.Inject

class FolderViewModel @Inject constructor(
    private val repo: FolderRepository
) : BaseViewModel() {
    fun getAllFolders(): Flow<List<Folder>> = repo.getAllFolders()
    fun getNotesInFolder(folderId: Int): Flow<List<Note>> = repo.getNotesInFolder(folderId)

    fun getAllFoldersSortByNameASC(): Flow<List<Folder>> = repo.getAllFoldersSortByNameASC()

    fun getAllFoldersSortByNameDESC(): Flow<List<Folder>> =
        repo.getAllFoldersSortByNameDESC()

    fun getAllFoldersLastOpenedNewest(): Flow<List<Folder>> = repo.getAllFoldersLastOpenedNewest()

    fun getAllFoldersLastOpenedOldest(): Flow<List<Folder>> = repo.getAllFoldersLastOpenedOldest()

    fun getAllFoldersLastEditedNewest(): Flow<List<Folder>> = repo.getAllFoldersLastEditedNewest()

    fun getAllFoldersLastEditedOldest(): Flow<List<Folder>> = repo.getAllFoldersLastEditedOldest()

    suspend fun getAllFoldersFavorite(): Flow<List<Folder>> = repo.getAllFoldersFavorite()

    fun getFolderByTag(tag: String): Flow<List<Folder>> = repo.getFolderByTag(tag)

    fun updateLastOpenedFolder(time: Long, folderId: Int) =
        repo.updateLastOpenedFolder(time, folderId)

    suspend fun insertFolder(folder: Folder): Long = repo.insertFolder(folder)
    suspend fun updateFolder(folder: Folder) = repo.updateFolder(folder)
    suspend fun deleteFolder(folder: Folder) = repo.deleteFolder(folder)

}

class FolderViewModelFactory @Inject constructor(
    private val repo: FolderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FolderViewModel(
                repo
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }

}