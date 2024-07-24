package ru.tfk.database.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import ru.tfk.database.model.PhotoModel

class PhotoModelListConverter {
    @TypeConverter
    fun fromListToString(list: List<PhotoModel>): String = Json.encodeToJsonElement(list).toString()

    @TypeConverter
    fun fromStringToList(string: String): List<PhotoModel> = Json.decodeFromString<List<PhotoModel>>(string)
}
