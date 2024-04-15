package code.with.me.testroomandnavigationdrawertest.domain.repo

import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteTag
import kotlinx.coroutines.flow.Flow

interface NoteTagRepository {
    fun getAllTags(): List<NoteTag>

    suspend fun insertTag(noteTag: NoteTag)
}
