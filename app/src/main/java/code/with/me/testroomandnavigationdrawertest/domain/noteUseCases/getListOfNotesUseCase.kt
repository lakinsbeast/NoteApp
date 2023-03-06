package code.with.me.testroomandnavigationdrawertest.domain.noteUseCases

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.flow.Flow

class getListOfNotesUseCase(
    private val noteRepository: NoteRepository
) {

    operator fun invoke(): Flow<List<Note>> = noteRepository.getListOfNotes()
}