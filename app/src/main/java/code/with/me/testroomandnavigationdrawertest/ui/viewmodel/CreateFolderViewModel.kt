package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class CreateFolderViewModel
    @Inject
    constructor(
        private val repo: FolderRepository,
//        private val dataStoreManager: DataStoreManager,
    ) : BaseViewModel() {
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

        companion object {
            class CreateFolderViewModelFactory
                @Inject
                constructor(
                    private val repo: FolderRepository,
//                    private val dataStoreManager: DataStoreManager,
                ) : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(CreateFolderViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return CreateFolderViewModel(
                                repo,
//                                dataStoreManager,
                            ) as T
                        }
                        throw IllegalArgumentException("ukn VM class")
                    }
                }
        }
    }
