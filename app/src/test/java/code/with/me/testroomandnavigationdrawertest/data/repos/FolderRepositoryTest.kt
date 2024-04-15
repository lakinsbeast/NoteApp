package code.with.me.testroomandnavigationdrawertest.data.repos

import androidx.test.core.app.ApplicationProvider
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderTagDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDatabase
import org.junit.Before

class FolderRepositoryTest {

    private lateinit var folderDao: FolderDAO
    private lateinit var folderTagDao: FolderTagDAO

    private lateinit var repository: FolderRepositoryImpl

    @Before
    fun createRepository() {
        folderDao
    }

}