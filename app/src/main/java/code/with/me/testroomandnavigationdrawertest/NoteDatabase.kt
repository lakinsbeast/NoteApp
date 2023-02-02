package code.with.me.testroomandnavigationdrawertest

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Note::class], exportSchema = false, version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDAO


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