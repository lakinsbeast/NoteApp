package ru.tfk.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
//        private val dataStoreManager: DataStoreManager,
    ) : ViewModel() {
//        private val dataStoreManager: DataStoreManager = DataStoreManager(Application().applicationContext)
        private val _isUseFolderEnabled = MutableStateFlow(false)
        val isUseFolderEnabled = _isUseFolderEnabled.asStateFlow()

        private val _isUseBehindBlurEnabled = MutableStateFlow(false)
        val isUseBehindBlurEnabled = _isUseBehindBlurEnabled.asStateFlow()

        private val _isUseBackgroundBlurEnabled = MutableStateFlow(false)
        val isUseBackgroundBlurEnabled = _isUseBackgroundBlurEnabled.asStateFlow()

        init {
//            viewModelScope.launch {
//                dataStoreManager.useFolderFlow.collect {
//                    _isUseFolderEnabled.emit(it)
//                }
//            }
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

        sealed class SettingAction {
            class ApplyFolderSettings(val flag: Boolean) : SettingAction()

            class ApplyBehindBlurSettings(val flag: Boolean) : SettingAction()

            class ApplyBackgroundBlurSettings(val flag: Boolean) : SettingAction()
        }

        /** сохранение настроек */
        fun saveSetting(whichSetting: SettingAction) {
            when (whichSetting) {
                is SettingAction.ApplyBackgroundBlurSettings -> {
                    applyUseBackgroundBlur(whichSetting.flag)
                }

                is SettingAction.ApplyBehindBlurSettings -> {
                    applyUseBehindBlur(whichSetting.flag)
                }

                is SettingAction.ApplyFolderSettings -> {
                    applyUseFolder(whichSetting.flag)
                }
            }
        }

        private fun applyUseFolder(useFolder: Boolean) {
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
//                dataStoreManager.saveUseFolderSettings(useFolder)
            }
        }

        private fun applyUseBehindBlur(useBlur: Boolean) {
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
//                dataStoreManager.saveUseBehindBlurSettings(useBlur)
            }
        }

        private fun applyUseBackgroundBlur(useBlur: Boolean) {
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
//                dataStoreManager.saveUseBackgroundBlurSettings(useBlur)
            }
        }
    }
