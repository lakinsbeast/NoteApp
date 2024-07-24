package ru.tfk.data.repository

// черновик not used
// data class UserSettings(
//    val id: Int = randomId(),
//    val useFolder: Boolean = true,
// )
//
// interface SettingsRepository {
//    suspend fun applyUseFolder(useFolder: Boolean)
//
//    suspend fun getUseFolderSettings(): Boolean
// }
//
// class SettingsRepositoryImpl
//    @Inject
//    constructor(
//        val dataStoreManager: DataStoreManager,
//    ) : SettingsRepository {
//        override suspend fun applyUseFolder(useFolder: Boolean) {
//            dataStoreManager.saveUseFolderSettings(useFolder)
//        }
//
//        override suspend fun getUseFolderSettings(): Boolean {
//            return true
//        }
//    }
