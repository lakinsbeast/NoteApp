package code.with.me.testroomandnavigationdrawertest

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KParameter

@Database(entities = [Note::class], exportSchema = false, version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDAO

    private class NoteDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val noteDAO = database.noteDao()
                }
            }
        }

    }


    companion object {
        // Singleton предотвращает одновременное открытие нескольких экземпляров базы данных //
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDb(ctx: Context, scope: CoroutineScope): NoteDatabase {
            // если ЭКЗЕМПЛЯР не равен null, то верните его,
            // если равен этому, то создайте базу данных
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(ctx.applicationContext, NoteDatabase::class.java, "note").build()
                INSTANCE = instance
                //return instance
                instance
            }
        }
    }
}