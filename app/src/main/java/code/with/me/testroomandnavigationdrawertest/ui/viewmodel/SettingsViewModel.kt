package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SettingsViewModel
    @Inject
    constructor(
        private val dataStoreManager: DataStoreManager,
    ) : ViewModel() {
        private val _isUseFolderEnabled = MutableLiveData<Boolean>()
        val isUseFolderEnabled = _isUseFolderEnabled

        private val _isUseBehindBlurEnabled = MutableLiveData<Boolean>()
        val isUseBehindBlurEnabled = _isUseBehindBlurEnabled

        private val _isUseBackgroundBlurEnabled = MutableLiveData<Boolean>()
        val isUseBackgroundBlurEnabled = _isUseBackgroundBlurEnabled

        init {
            viewModelScope.launch {
                dataStoreManager.useFolderFlow.collect {
                    _isUseFolderEnabled.postValue(it)
                }
            }
            viewModelScope.launch {
                dataStoreManager.useBehindBlurFlow.collect {
                    _isUseBehindBlurEnabled.postValue(it)
                }
            }
            viewModelScope.launch {
                dataStoreManager.useBackgroundBlurFlow.collect {
                    _isUseBackgroundBlurEnabled.postValue(it)
                }
            }
        }

        fun applyUseFolder(useFolder: Boolean) {
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                dataStoreManager.saveUseFolderSettings(useFolder)
            }
        }

        fun applyUseBehindBlur(useBlur: Boolean) {
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                dataStoreManager.saveUseBehindBlurSettings(useBlur)
            }
        }

        fun applyUseBackgroundBlur(useBlur: Boolean) {
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                dataStoreManager.saveUseBackgroundBlurSettings(useBlur)
            }
        }
    }

class SettingsViewModelFactory
    @Inject
    constructor(
        private val dataStoreManager: DataStoreManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(
                    dataStoreManager,
                ) as T
            }
            throw IllegalArgumentException("ukn VM class")
        }
    }
