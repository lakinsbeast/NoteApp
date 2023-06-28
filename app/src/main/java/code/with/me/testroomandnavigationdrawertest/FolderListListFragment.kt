package code.with.me.testroomandnavigationdrawertest

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFolderListFragment
import kotlinx.coroutines.launch

class FolderListListFragment :
    BaseFolderListFragment() {

    private var listOfFolders = mutableListOf<Folder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            folderViewModel.getAllFolders().collect() {
                listOfFolders.clear()
                listOfFolders = it.toMutableList()
                adapter.submitList(listOfFolders)
                adapter.notifyDataSetChanged()
            }
        }
    }


}