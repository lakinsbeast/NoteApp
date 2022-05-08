package code.with.me.testroomandnavigationdrawertest

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NotesApplication: Application() {
    // Нет необходимости отменять эту область, так как она будет уничтожена вместе с процессом
    val applicationScope = CoroutineScope(SupervisorJob())

    val db by lazy { NoteDatabase.getDb(this, applicationScope) }
    val repo by lazy { NoteRepository(db.noteDao())}

}