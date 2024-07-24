package ru.tfk.data.repository

import ru.tfk.database.model.NoteTag

interface NoteTagRepository {
    fun getAllTags(): List<NoteTag>

    suspend fun insertTag(noteTag: NoteTag)
}
