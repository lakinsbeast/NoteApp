package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class FolderViewModel
    @Inject
    constructor(
        private val repo: FolderRepository,
//        private val dataStoreManager: DataStoreManager,
    ) : BaseViewModel() {
        fun getAllFolders(): Flow<List<Folder>> = repo.getAllFolders()

        fun getNotesInFolder(folderId: Long): Flow<List<Note>> = repo.getNotesInFolder(folderId)

        fun getAllFoldersSortByNameASC(): Flow<List<Folder>> = repo.getAllFoldersSortByNameASC()

        fun getAllFoldersSortByNameDESC(): Flow<List<Folder>> = repo.getAllFoldersSortByNameDESC()

        fun getAllFoldersLastOpenedNewest(): Flow<List<Folder>> = repo.getAllFoldersLastOpenedNewest()

        fun getAllFoldersLastOpenedOldest(): Flow<List<Folder>> = repo.getAllFoldersLastOpenedOldest()

        fun getAllFoldersLastEditedNewest(): Flow<List<Folder>> = repo.getAllFoldersLastEditedNewest()

        fun getAllFoldersLastEditedOldest(): Flow<List<Folder>> = repo.getAllFoldersLastEditedOldest()

        suspend fun getAllFoldersFavorite(): Flow<List<Folder>> = repo.getAllFoldersFavorite()

        fun getFolderByTag(tag: String): Flow<List<Folder>> = repo.getFolderByTag(tag)

        fun updateLastOpenedFolder(
            time: Long,
            folderId: Long,
        ) = repo.updateLastOpenedFolder(time, folderId)

        suspend fun insertFolder(folder: Folder): Long = repo.insertFolder(folder)

        suspend fun updateFolder(folder: Folder) = repo.updateFolder(folder)

        suspend fun deleteFolder(folder: Folder) = repo.deleteFolder(folder)
    }

sealed class FolderVMState {
    object Loading : FolderVMState()

    class Result<T>(val data: T) : FolderVMState()

    object EmptyResult : FolderVMState()

    class Error<T>(val error: T) : FolderVMState()
}

class FolderViewModelFactory
    @Inject
    constructor(
        private val repo: FolderRepository,
//        private val dataStoreManager: DataStoreManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FolderViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FolderViewModel(
                    repo,
//                    dataStoreManager,
                ) as T
            }
            throw IllegalArgumentException("ukn VM class")
        }
    }
