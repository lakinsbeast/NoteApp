package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderTagRepository
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject

class FolderTagViewModel
    @Inject
    constructor(
        private val repo: FolderTagRepository,
    ) : BaseViewModel() {
        fun getAllTags() = repo.getAllTags()

        suspend fun insertTag(folderTag: FolderTag) = repo.insertTag(folderTag)
    }

class FolderTagViewModelFactory
    @Inject
    constructor(
        private val repo: FolderTagRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FolderTagViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FolderTagViewModel(
                    repo,
                ) as T
            }
            throw IllegalArgumentException("ukn VM class")
        }
    }
