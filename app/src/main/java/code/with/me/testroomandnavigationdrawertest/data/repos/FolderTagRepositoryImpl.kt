package code.with.me.testroomandnavigationdrawertest.data.repos

import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderTagDAO
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderTagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FolderTagRepositoryImpl @Inject constructor(private val dao: FolderTagDAO) : FolderTagRepository {
    override fun getAllTags(): Flow<List<FolderTag>> = dao.getAllTags()
    override suspend fun insertTag(folderTag: FolderTag) = dao.insertTag(folderTag)
}