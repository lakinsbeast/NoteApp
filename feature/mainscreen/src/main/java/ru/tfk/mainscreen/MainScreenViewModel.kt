package ru.tfk.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.tfk.data.repository.FolderRepository
import ru.tfk.data.repository.NoteRepository
import ru.tfk.database.model.Folder
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel
    @Inject
    constructor(
        private val repoNote: NoteRepository,
        private val repoFolder: FolderRepository,
//        private val dataStoreManager: DataStoreManager,
    ) : ViewModel() {
        private val _selectedChip = MutableStateFlow(-1)
        val selectedChip = _selectedChip.asStateFlow()

        /** todo понять зачем нужен в принципе нужен selectedChipFolderId */
        private val _selectedChipFolderId = MutableStateFlow(1L)
        val selectedChipFolderId = _selectedChipFolderId.asStateFlow()

        fun getAllFolders(): Flow<List<Folder>> = repoFolder.getAllFolders()

        private val _isUseFolderEnabled = MutableStateFlow(false)
        val isUseFolderEnabled = _isUseFolderEnabled.asStateFlow()

        init {
            /** нужно ли использовать папки, если false, то скрывает вьюху папок */
            viewModelScope.launch {
//                dataStoreManager.useFolderFlow.collect {
//                    _isUseFolderEnabled.emit(it)
//                }
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

sealed class MainScreenState

sealed class MainScreenStateAction

class MainScreenViewModelFactory
    @Inject
    constructor(
        private val repoNote: NoteRepository,
        private val repoFolder: FolderRepository,
//        private val dataStoreManager: DataStoreManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainScreenViewModel(
                    repoNote,
                    repoFolder,
//                    dataStoreManager,
                ) as T
            }
            throw IllegalArgumentException("ukn VM class")
        }
    }
