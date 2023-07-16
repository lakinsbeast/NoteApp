package code.with.me.testroomandnavigationdrawertest.data.localDataSource.converters

import androidx.room.TypeConverter
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

class PhotoModelListConverter {
    @TypeConverter
    fun fromListToString(list: List<PhotoModel>): String = Json.encodeToJsonElement(list).toString()
    @TypeConverter
    fun fromStringToList(string: String): List<PhotoModel> =
        Json.decodeFromString<List<PhotoModel>>(string)

}