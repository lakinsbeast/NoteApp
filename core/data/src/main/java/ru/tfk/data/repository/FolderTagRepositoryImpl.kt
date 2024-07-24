package ru.tfk.data.repository

import kotlinx.coroutines.flow.Flow
import ru.tfk.database.dao.FolderTagDAO
import ru.tfk.database.model.FolderTag
import javax.inject.Inject

class FolderTagRepositoryImpl
    @Inject
    constructor(private val dao: FolderTagDAO) : FolderTagRepository {
        override fun getAllTags(): Flow<List<FolderTag>> = dao.getAllTags()

        override suspend fun insertTag(folderTag: FolderTag) = dao.insertTag(folderTag)
    }
