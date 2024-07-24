package ru.tfk.createfolder

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ru.tfk.data.repository.FolderRepository
import ru.tfk.database.model.Folder
import javax.inject.Inject

@HiltViewModel
class CreateFolderViewModel
    @Inject
    constructor(
        private val repo: FolderRepository,
//        private val dataStoreManager: DataStoreManager,
    ) : ViewModel() {
        // todo что-то сделать с ними, похоже не нужны
        var selectedChip = -2
        var selectedText = ""

        private val _isUseBehindBlurEnabled = MutableStateFlow(false)
        val isUseBehindBlurEnabled = _isUseBehindBlurEnabled

        private val _isUseBackgroundBlurEnabled = MutableStateFlow(false)
        val isUseBackgroundBlurEnabled = _isUseBackgroundBlurEnabled

        init {
//            viewModelScope.launch {
//                dataStoreManager.useBehindBlurFlow.collect {
//                    _isUseBehindBlurEnabled.emit(it)
//                }
//            }
//            viewModelScope.launch {
//                dataStoreManager.useBackgroundBlurFlow.collect {
//                    _isUseBackgroundBlurEnabled.emit(it)
//                }
//            }
        }

        // вставляем в БД новую папку
        suspend fun insertFolder(folder: Folder): Long = repo.insertFolder(folder)
    }
