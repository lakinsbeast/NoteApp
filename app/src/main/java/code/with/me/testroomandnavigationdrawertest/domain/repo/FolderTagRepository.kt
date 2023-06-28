package code.with.me.testroomandnavigationdrawertest.domain.repo

import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import kotlinx.coroutines.flow.Flow

interface FolderTagRepository {
    fun getAllTags(): Flow<List<FolderTag>>

    suspend fun insertTag(folderTag: FolderTag)
}