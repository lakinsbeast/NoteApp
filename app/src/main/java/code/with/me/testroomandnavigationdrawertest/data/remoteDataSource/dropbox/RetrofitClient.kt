package code.with.me.testroomandnavigationdrawertest.data.remoteDataSource.dropbox

import code.with.me.testroomandnavigationdrawertest.data.data_classes.DropboxInfo
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun getClient(): Retrofit {
        val contentType = MediaType.get("application/x-www-form-urlencoded")

        return Retrofit.Builder().baseUrl(DropboxInfo.GET_TOKEN_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}
