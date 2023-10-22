package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.DataStoreManager
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class FolderViewModel @Inject constructor(
    private val repo: FolderRepository,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {

    private val _state = MutableLiveData<FolderVMState>(FolderVMState.Loading)
    val state: LiveData<FolderVMState>
        get() = _state

    private fun setState(state: FolderVMState) {
        _state.postValue(state)
    }
    private val _isUseBehindBlurEnabled = MutableLiveData<Boolean>()
    val isUseBehindBlurEnabled = _isUseBehindBlurEnabled

    private val _isUseBackgroundBlurEnabled = MutableLiveData<Boolean>()
    val isUseBackgroundBlurEnabled = _isUseBackgroundBlurEnabled

    init {
        viewModelScope.launch {
            dataStoreManager.useBehindBlurFlow.collect() {
                _isUseBehindBlurEnabled.postValue(it)
            }
        }
        viewModelScope.launch {
            dataStoreManager.useBackgroundBlurFlow.collect() {
                _isUseBackgroundBlurEnabled.postValue(it)
            }
        }
    }

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

sealed class FolderVMState {
    data object Loading : FolderVMState()
    class Result<T>(val data: T) : FolderVMState()
    data object EmptyResult : FolderVMState()
    class Error<T>(val error: T) : FolderVMState()
}

class FolderViewModelFactory @Inject constructor(
    private val repo: FolderRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FolderViewModel(
                repo,
                dataStoreManager
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }

}