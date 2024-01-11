package code.with.me.testroomandnavigationdrawertest.data.data_classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DropboxTokenDTO(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("account_id")
    val accountId: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("scope")
    val scope: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("uid")
    val uid: String,
)
