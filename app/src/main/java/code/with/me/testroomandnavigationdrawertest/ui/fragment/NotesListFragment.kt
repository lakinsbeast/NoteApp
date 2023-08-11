package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.println
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NewNote
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseNotesListFragment
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionNote
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class NotesListFragment :
    BaseNotesListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idFolder = arguments?.getInt("idFolder") ?: -1
//        if (idFolder == -1) {
//            findNavController().popBackStack()
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            noteViewModel.state.observe(viewLifecycleOwner) { state ->
                handleViewState(state)
            }
            noteViewModel.userActionState.observe(viewLifecycleOwner) { state ->
                handleUserActionState(state)
            }
            if (idFolder == -1) {
                noteViewModel.getAllNotes()
            } else {
                noteViewModel.getAllNotes(idFolder)
            }
        }
    }


    private fun handleUserActionState(state: UserActionNote) {
        binding.apply {
            when (state) {
                is UserActionNote.SavedNoteToDB -> {

                }

                else -> {}
            }
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }

    private fun handleViewState(state: NoteState) {
        when (state) {
            is NoteState.Loading -> {
                showProgressBar(true)
            }

            is NoteState.Result<*> -> {
                val newNoteList = state.data as List<Note>
                val note: ArrayList<NewNote> = ArrayList()
                newNoteList.forEach {
                    note.add(it.toNewNote())
                }
                adapter.submitList(note)
                adapter.notifyDataSetChanged()
                showProgressBar(false)
            }

            is NoteState.Error<*> -> {
                showProgressBar(false)
                Snackbar.make(binding.root, state.error.toString(), Snackbar.LENGTH_LONG).show()
                println("ERROR in ${this.javaClass.simpleName} error: ${state.error}")
            }

            is NoteState.EmptyResult -> {
                showProgressBar(false)
            }
        }
    }

}