package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.DataStoreManager
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class MainScreenViewModel(
    private val repoNote: NoteRepository,
    private val repoFolder: FolderRepository,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {

//    private var selectedChip = -1
//    private var selectedChipFolderId = 1L

    private val _selectedChip = MutableStateFlow(-1)
    val selectedChip = _selectedChip.asStateFlow()

    private val _selectedChipFolderId = MutableStateFlow(1L)
    val selectedChipFolderId = _selectedChipFolderId.asStateFlow()
    fun getAllFolders(): Flow<List<Folder>> = repoFolder.getAllFolders()

    private val _isUseFolderEnabled = MutableStateFlow(false)
    val isUseFolderEnabled = _isUseFolderEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.useFolderFlow.collect {
                _isUseFolderEnabled.emit(it)
            }
        }
    }

    fun setSelectedChip(value: Int) {
        viewModelScope.launch {
            _selectedChip.emit(value)
        }
    }

    fun setSelectedChipFolderId(value: Long) {
        viewModelScope.launch {
            _selectedChipFolderId.emit(value)
        }
    }

    fun getSelectedChip() = selectedChip.value

    fun getAllNotes() = repoNote.getListOfNotes()

    fun getAllNotes(id: Long) = repoNote.getListOfNotes(id)
}

class MainScreenViewModelFactory
@Inject
constructor(
    private val repoNote: NoteRepository,
    private val repoFolder: FolderRepository,
    private val dataStoreManager: DataStoreManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainScreenViewModel(
                repoNote,
                repoFolder,
                dataStoreManager,
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }
}
