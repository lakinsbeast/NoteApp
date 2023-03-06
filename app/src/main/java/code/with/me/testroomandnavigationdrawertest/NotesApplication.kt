package code.with.me.testroomandnavigationdrawertest

import android.app.Application
import code.with.me.testroomandnavigationdrawertest.data.di.AppComponent
import code.with.me.testroomandnavigationdrawertest.data.di.DaggerAppComponent
import code.with.me.testroomandnavigationdrawertest.data.di.DatabaseModule
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDatabase
import code.with.me.testroomandnavigationdrawertest.domain.di.UseCaseModule
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.deleteNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.getListOfNotesUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.insertNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.updateNoteUseCase
import code.with.me.testroomandnavigationdrawertest.ui.di.ApplicationModule
//import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NotesApplication : Application() {

    companion object {

    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().applicationModule(ApplicationModule(this))
            .databaseModule(DatabaseModule(this)).useCaseModule(
            UseCaseModule()
        ).build()
    }

    // Нет необходимости отменять эту область, так как она будет уничтожена вместе с процессом
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

//        appComponent = DaggerAppComponent.builder().build().inject(this)
    }


}