package code.with.me.testroomandnavigationdrawertest.domain.di

import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderTagDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteTagDAO
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderTagRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteTagRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module()
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    fun provideNoteRepositoryModule(noteDAO: NoteDAO): NoteRepository = NoteRepositoryImpl(noteDAO)

    @Provides
    fun provideFolderRepositoryModule(
        folderDao: FolderDAO,
        folderTagDAO: FolderTagDAO,
    ): FolderRepository = FolderRepositoryImpl(folderDao, folderTagDAO)

    @Provides
    fun provideFolderTagRepositoryModule(folderTagDAO: FolderTagDAO): FolderTagRepository = FolderTagRepositoryImpl(folderTagDAO)

    @Provides
    fun provideNoteTagRepositoryModule(noteTagDAO: NoteTagDAO): NoteTagRepository = NoteTagRepositoryImpl(noteTagDAO)
}
