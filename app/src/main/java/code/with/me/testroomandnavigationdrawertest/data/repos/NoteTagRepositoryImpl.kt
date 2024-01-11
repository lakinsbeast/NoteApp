package code.with.me.testroomandnavigationdrawertest.data.repos

import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteTag
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteTagDAO
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteTagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteTagRepositoryImpl
    @Inject
    constructor(private val dao: NoteTagDAO) : NoteTagRepository {
        override fun getAllTags(): Flow<List<NoteTag>> = dao.getAllTags()

        override suspend fun insertTag(noteTag: NoteTag) = dao.insertTag(noteTag)
    }
