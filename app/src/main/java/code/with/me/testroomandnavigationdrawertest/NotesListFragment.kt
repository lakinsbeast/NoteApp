package code.with.me.testroomandnavigationdrawertest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NewNote
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentNotesListBinding
import code.with.me.testroomandnavigationdrawertest.databinding.NoteItemBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseNotesListFragment
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class NotesListFragment :
    BaseNotesListFragment() {

    private val NewNotesArray: ArrayList<NewNote> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            noteViewModel.getAllNotes(idFolder).collect { notes ->
                if (notes.isNotEmpty()) {
                    NewNotesArray.clear()
                    println("notes size: ${notes.size}")
                    notes.forEach {
                        NewNotesArray.add(it.toNewNote())
                    }
                    adapter.submitList(NewNotesArray as MutableList<NewNote>)
                    binding.rv.adapter?.notifyDataSetChanged()
                } else {
                    binding.apply {
//                        welcomeLayout.visibility = View.VISIBLE
                    }
                }
                changeUiOnRvUpdate(binding, notes)
            }
        }
    }


}