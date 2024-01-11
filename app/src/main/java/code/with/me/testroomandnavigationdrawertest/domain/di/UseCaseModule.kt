package code.with.me.testroomandnavigationdrawertest.domain.di

import code.with.me.testroomandnavigationdrawertest.data.repos.FolderRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderTagRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteTagRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module()
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNoteRepositoryModule(noteRepositoryImpl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindFolderRepositoryModule(folderRepositoryImpl: FolderRepositoryImpl): FolderRepository

    @Binds
    @Singleton
    abstract fun bindFolderTagRepositoryModule(folderTagRepositoryImpl: FolderTagRepositoryImpl): FolderTagRepository

    @Binds
    @Singleton
    abstract fun bindNoteTagRepositoryModule(noteTagRepositoryImpl: NoteTagRepositoryImpl): NoteTagRepository
}
