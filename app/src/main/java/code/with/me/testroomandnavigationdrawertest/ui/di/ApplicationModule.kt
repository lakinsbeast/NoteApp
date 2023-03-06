package code.with.me.testroomandnavigationdrawertest.ui.di

import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.deleteNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.getListOfNotesUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.insertNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.updateNoteUseCase
import code.with.me.testroomandnavigationdrawertest.ui.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.NoteViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
abstract class BindAppModule {
    @Binds
    abstract fun bindViewModelFactory(factory: NoteViewModelFactory): ViewModelProvider.Factory
}

@Module
class ApplicationModule(private val application: NotesApplication) {

    @Provides
    @Singleton
    fun provideApp(): NotesApplication = application

    @Provides
    fun provideNoteViewModel(
        deleteNoteUseCase: deleteNoteUseCase,
        getListOfNotesUseCase: getListOfNotesUseCase,
        insertNoteUseCase: insertNoteUseCase,
        updateNoteUseCase: updateNoteUseCase
    ) = NoteViewModel(deleteNoteUseCase,getListOfNotesUseCase,insertNoteUseCase,updateNoteUseCase)


}
