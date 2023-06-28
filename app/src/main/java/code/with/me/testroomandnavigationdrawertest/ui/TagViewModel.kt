package code.with.me.testroomandnavigationdrawertest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderTagRepository
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject

class TagViewModel @Inject constructor(
    private val repo: FolderTagRepository
) : BaseViewModel() {

    fun getAllTags() = repo.getAllTags()

    suspend fun insertTag(folderTag: FolderTag) = repo.insertTag(folderTag)

}

class TagViewModelFactory @Inject constructor(
    private val repo: FolderTagRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TagViewModel(
                repo
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }

}