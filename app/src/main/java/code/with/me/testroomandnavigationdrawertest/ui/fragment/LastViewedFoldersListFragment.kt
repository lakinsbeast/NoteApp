package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFolderListFragment
import kotlinx.coroutines.launch

class LastViewedFoldersListFragment :
    BaseFolderListFragment() {
    // I had to create this, because notifydatasetchanged did not work when updating an item
    private var listOfFolders = mutableListOf<Folder>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            folderViewModel.getAllFoldersLastEditedNewest().collect {
                listOfFolders.clear()
                listOfFolders = it.toMutableList()
                adapter.submitList(listOfFolders)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
