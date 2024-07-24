package ru.tfk.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.tfk.data.repository.FolderRepository
import ru.tfk.data.repository.FolderRepositoryImpl
import ru.tfk.data.repository.FolderTagRepository
import ru.tfk.data.repository.FolderTagRepositoryImpl
import ru.tfk.data.repository.NoteRepository
import ru.tfk.data.repository.NoteRepositoryImpl
import ru.tfk.data.repository.NoteTagRepository
import ru.tfk.data.repository.NoteTagRepositoryImpl
import ru.tfk.database.dao.FolderDAO
import ru.tfk.database.dao.FolderTagDAO
import ru.tfk.database.dao.NoteDAO
import ru.tfk.database.dao.NoteTagDAO

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {
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
