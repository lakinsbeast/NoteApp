package code.with.me.testroomandnavigationdrawertest.data.data_classes

import com.google.gson.annotations.SerializedName

data class DropboxTokenDTOO(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("scope")
    val scope: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("uid")
    val uid: String,
)
