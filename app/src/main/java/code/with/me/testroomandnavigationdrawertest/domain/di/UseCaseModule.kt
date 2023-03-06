package code.with.me.testroomandnavigationdrawertest.domain.di

import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.deleteNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.getListOfNotesUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.insertNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.updateNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [UseCaseModule::class])
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepositoryModule(noteRepositoryImpl: NoteRepositoryImpl): NoteRepository
}

@Module
class UseCaseModule {

    @Provides
    fun provideDeleteNoteUseCase(noteRepository: NoteRepository): deleteNoteUseCase =
        deleteNoteUseCase(noteRepository)

    @Provides
    fun provideGetListOfNotesUseCase(noteRepository: NoteRepository): getListOfNotesUseCase =
        getListOfNotesUseCase(noteRepository)

    @Provides
    fun provideInsertNoteUseCase(noteRepository: NoteRepository): insertNoteUseCase =
        insertNoteUseCase(noteRepository)

    @Provides
    fun provideUpdateNoteUseCase(noteRepository: NoteRepository): updateNoteUseCase =
        updateNoteUseCase(noteRepository)
}
