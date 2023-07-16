package code.with.me.testroomandnavigationdrawertest

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFolderListFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AudioRecorderSheet
import kotlinx.coroutines.launch

class FolderListFragment :
    BaseFolderListFragment() {

    private var listOfFolders = mutableListOf<Folder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            folderViewModel.getAllFolders().collect() {
                listOfFolders.clear()
                listOfFolders = it.toMutableList()
                adapter.submitList(listOfFolders)
            }
        }

    }

    fun updateRecyclerViewData(checkedChipId: String) {
        //arrayList(3,4) -> "3,4"
        println("checkedChipId: $checkedChipId")
        if (checkedChipId.isEmpty()) {
            lifecycleScope.launch {
                folderViewModel.getAllFolders().collect() {
                    listOfFolders.clear()
                    listOfFolders = it.toMutableList()
                    adapter.submitList(listOfFolders)
                }
            }
        } else {
            lifecycleScope.launch {
                folderViewModel.getFolderByTag(checkedChipId).collect() {
                    listOfFolders.clear()
                    listOfFolders = it.toMutableList()
                    adapter.submitList(listOfFolders)
                }
            }
        }
    }


}