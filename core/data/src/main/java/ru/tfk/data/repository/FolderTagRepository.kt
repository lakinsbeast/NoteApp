package ru.tfk.data.repository

import kotlinx.coroutines.flow.Flow
import ru.tfk.database.model.FolderTag

interface FolderTagRepository {
    fun getAllTags(): Flow<List<FolderTag>>

    suspend fun insertTag(folderTag: FolderTag)
}
