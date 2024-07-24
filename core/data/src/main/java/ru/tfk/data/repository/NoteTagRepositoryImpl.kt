package ru.tfk.data.repository

import ru.tfk.database.dao.NoteTagDAO
import ru.tfk.database.model.NoteTag
import javax.inject.Inject

class NoteTagRepositoryImpl
    @Inject
    constructor(private val dao: NoteTagDAO) : NoteTagRepository {
        override fun getAllTags(): List<NoteTag> = dao.getAllTags()

        override suspend fun insertTag(noteTag: NoteTag) = dao.insertTag(noteTag)
    }
