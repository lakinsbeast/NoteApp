package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = USER_SETTINGS,
        )

        val useFolderFlow: Flow<Boolean> =
            context.dataStore.data
                .map { preferences ->
                    preferences[KEY_USE_FOLDER] ?: true
                }

        val useBehindBlurFlow: Flow<Boolean> =
            context.dataStore.data
                .map { preferences ->
                    preferences[KEY_USE_BEHIND_BLUR] ?: true
                }
        val useBackgroundBlurFlow: Flow<Boolean> =
            context.dataStore.data
                .map { preferences ->
                    preferences[KEY_USE_BACKGROUND_BLUR] ?: false
                }

        suspend fun saveUseFolderSettings(useFolder: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[KEY_USE_FOLDER] = useFolder
            }
        }

        suspend fun saveUseBehindBlurSettings(useBlur: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[KEY_USE_BEHIND_BLUR] = useBlur
            }
        }

        suspend fun saveUseBackgroundBlurSettings(useBlur: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[KEY_USE_BACKGROUND_BLUR] = useBlur
            }
        }

        suspend fun clearUserSettings() {
            context.dataStore.edit { preferences ->
                preferences[KEY_USERNAME] = ""
                preferences[KEY_ID] = ""
            }
        }

        companion object {
            const val USER_SETTINGS = "user_settings"
            private val KEY_USERNAME = stringPreferencesKey("key_username")
            private val KEY_USE_FOLDER = booleanPreferencesKey("key_use_folder")
            private val KEY_USE_BEHIND_BLUR = booleanPreferencesKey("key_use_behind_blur")
            private val KEY_USE_BACKGROUND_BLUR = booleanPreferencesKey("key_use_background_blur")

            private val KEY_ID = stringPreferencesKey("key_user_id")
        }
    }
