package code.with.me.testroomandnavigationdrawertest.data.remoteDataSource.dropbox

import code.with.me.testroomandnavigationdrawertest.data.data_classes.DropboxInfo
import code.with.me.testroomandnavigationdrawertest.data.data_classes.DropboxTokenDTOO
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface getTokenApi {
    @POST("oauth2/token/")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getToken(
        @Query("code") code: String,
        @Query("grant_type") grant_type: String = "authorization_code",
        @Query("client_id") client_id: String = DropboxInfo.CLIENT_APP,
        @Query("client_secret") client_secret: String = DropboxInfo.CLIENT_SECRET,
    ): Call<DropboxTokenDTOO>
}
