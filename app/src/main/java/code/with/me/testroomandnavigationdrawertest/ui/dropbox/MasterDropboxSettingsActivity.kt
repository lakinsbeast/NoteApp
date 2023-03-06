package code.with.me.testroomandnavigationdrawertest.ui.dropbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.data_classes.DropboxInfo
import code.with.me.testroomandnavigationdrawertest.data.data_classes.dataStoreInit
import code.with.me.testroomandnavigationdrawertest.data.remoteDataSource.dropbox.RetrofitClient
import code.with.me.testroomandnavigationdrawertest.data.remoteDataSource.dropbox.getTokenApi
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMasterDropboxSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

class MasterDropboxSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterDropboxSettingsBinding

    private lateinit var code: Flow<String>

    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterDropboxSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStore = this.dataStoreInit

        lifecycleScope.launch {
            code = this@MasterDropboxSettingsActivity.dataStore.data
                .catch {
                    if (it is IOException) {
                        code.map { "emptyCode" }
                    } else {
                        throw it
                    }
                }.map {
                    Log.d("datastoremap", it[DropboxInfo.FIELD_CODE].toString())
                    if (it[DropboxInfo.FIELD_CODE] == null) {
                        binding.authAndCodeLayout.visibility = View.VISIBLE
                    }
                    it[DropboxInfo.FIELD_CODE] ?: ""
                }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            code.collect {
                Log.d("datastorecollect", it.toString())
                if (it != "null") {
                    val token =
                        RetrofitClient.getClient().create(getTokenApi::class.java).getToken(it)
                            .execute()
                    Log.d("token", token.toString())
                    token.errorBody()!!.contentType().toString()
                }
            }
        }

        binding.authorizeBtn.setOnClickListener {
            binding.webViewFragment.visibility = View.VISIBLE
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.webViewFragment, AuthorizeDropboxFragment()).addToBackStack(null)
                .commit()
        }

        binding.saveCodeBtn.setOnClickListener {
            lifecycleScope.launch {
                writeCodeToDataStore(binding.codeTextField.text.toString())
            }
        }
    }

    init {

    }

    private suspend fun writeCodeToDataStore(code: String) {
        this.dataStore.edit {
            it[DropboxInfo.FIELD_CODE] = code
        }
    }
}