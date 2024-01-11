package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteTag
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteTagRepository
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject

class NoteTagViewModel
    @Inject
    constructor(
        private val repo: NoteTagRepository,
    ) : BaseViewModel() {
        fun getAllTags() = repo.getAllTags()

        suspend fun insertTag(noteTag: NoteTag) = repo.insertTag(noteTag)
    }

class NoteTagViewModelFactory
    @Inject
    constructor(
        private val repo: NoteTagRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteTagViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteTagViewModel(
                    repo,
                ) as T
            }
            throw IllegalArgumentException("ukn VM class")
        }
    }
