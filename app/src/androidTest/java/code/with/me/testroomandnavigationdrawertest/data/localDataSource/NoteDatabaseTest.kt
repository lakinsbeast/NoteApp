package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {
    private lateinit var db: NoteDatabase
    private lateinit var noteDao: NoteDAO
    private lateinit var folderDao: FolderDAO
    private lateinit var folderTagDAO: FolderTagDAO
    private lateinit var noteTagDAO: NoteTagDAO

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db =
            Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).allowMainThreadQueries()
                .build()
        noteDao = db.noteDao()
        folderDao = db.folderDao()
        folderTagDAO = db.folderTagDAO()
        noteTagDAO = db.noteTagDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun test() = runBlocking {}

//    @Test
//    fun getListOfNotes() =
//        runTest {
//            val note1 = Note(1, "title1", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            val note2 = Note(2, "title2", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            db.noteDao().insertNote(note1)
//            db.noteDao().insertNote(note2)
//            async {
//                db.noteDao().getListOfNotes().collect {
//                    println("getListOfNotes: ${it.size}")
//                    if (it.isNotEmpty()) {
//                        assertEquals(it.size, 2)
//                        cancel()
//                    }
//                }
//            }
//        }
//
//    @Test
//    fun getListOfNotesWithFolderId() =
//        runTest {
//            val note1 = Note(1, "title1", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            val note2 = Note(2, "title2", "text", listOf(), "audioUrl", "colorCard", 2, 1, 1, true, "")
//            val note3 = Note(3, "title2", "text", listOf(), "audioUrl", "colorCard", 2, 1, 1, true, "")
//            val note4 = Note(4, "title2", "text", listOf(), "audioUrl", "colorCard", 2, 1, 1, true, "")
//
//            db.noteDao().insertNote(note1)
//            db.noteDao().insertNote(note2)
//            db.noteDao().insertNote(note3)
//            db.noteDao().insertNote(note4)
//            async {
//                db.noteDao().getListOfNotes(2).collect {
//                    println("getListOfNotes: ${it.size}")
//                    if (it.isNotEmpty()) {
//                        assertEquals(it.size, 3)
//                        cancel()
//                    }
//                }
//            }
//        }
//
//    @Test
//    fun findByTitle() =
//        runTest {
//            val note1 = Note(1, "title1", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            val note2 = Note(2, "title2", "text", listOf(), "audioUrl", "colorCard", 2, 1, 1, true, "")
//            val note3 = Note(3, "title2", "text", listOf(), "audioUrl", "colorCard", 2, 1, 1, true, "")
//            val note4 = Note(4, "title45", "text", listOf(), "audioUrl", "colorCard", 2, 1, 1, true, "")
//
//            db.noteDao().insertNote(note1)
//            db.noteDao().insertNote(note2)
//            db.noteDao().insertNote(note3)
//            db.noteDao().insertNote(note4)
//            val note = db.noteDao().findByTitle("title1", "text")
//            assertEquals(note.titleNote, "title1")
//
//            val secondNote = db.noteDao().findByTitle("title2", "text")
//            assertEquals(secondNote.titleNote, "title2")
//
//            val thirdNote = db.noteDao().findByTitle("title45", "text")
//            assertEquals(thirdNote.titleNote, "title45")
//        }
//
//    @Test
//    fun getNoteById() =
//        runTest {
//        }
//
//    @Test
//    fun insertOrUpdate() =
//        runTest {
//        }
//
//    @Test
//    fun insertNote() =
//        runTest {
//            val note = Note(1, "title", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            db.noteDao().insertNote(note)
//            val getNote = db.noteDao().getNoteById(1)
//            assertEquals(getNote.titleNote, note.titleNote)
//        }
//
//    @Test
//    fun deleteNote() =
//        runTest {
//            val note = Note(1, "title1", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            db.noteDao().insertNote(note)
//            val getNote = db.noteDao().getNoteById(1)
//            assertEquals(getNote.titleNote, note.titleNote)
//            db.noteDao().deleteNote(getNote)
//            assertEquals(db.noteDao().getNoteById(1), null)
//        }
//
//    @Test
//    fun getLastCustomer() =
//        runTest {
//            val note = Note(1, "title1", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            val note1 = Note(2, "title1", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            val note2 = Note(3, "title1", "text", listOf(), "audioUrl", "colorCard", 1, 1, 1, true, "")
//            db.noteDao().insertNote(note)
//            db.noteDao().insertNote(note1)
//            db.noteDao().insertNote(note2)
//            assertEquals(db.noteDao().getLastCustomer(), 3)
//        }
}
