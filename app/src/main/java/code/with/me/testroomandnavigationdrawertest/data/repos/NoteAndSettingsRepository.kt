package code.with.me.testroomandnavigationdrawertest.data.repos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import code.with.me.testroomandnavigationdrawertest.data.const.const.Companion.nightModeFlag

class NoteAndSettingsRepository(private val dataStore: DataStore<Preferences>, ctx: Context) {

    val Context.dataStore by preferencesDataStore(
        name = nightModeFlag
    )


}