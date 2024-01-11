package code.with.me.testroomandnavigationdrawertest.data.data_classes

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import code.with.me.testroomandnavigationdrawertest.data.const.const

val Context.dataStoreInit by preferencesDataStore(
    name = const.appSettings,
)

object DropboxInfo {
    val FIELD_CODE = stringPreferencesKey("code")
    val CLIENT_APP = "qbfifjaqizhpc5t"
    val CLIENT_SECRET = "83njhmsgvfr2lht"
    val GET_TOKEN_URL = "https://api.dropboxapi.com/"
}
