package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NewNote
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseNotesListFragment
import kotlinx.coroutines.launch

class NotesListFragment :
    BaseNotesListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idFolder = arguments?.getInt("idFolder") ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            noteViewModel.state.observe(viewLifecycleOwner) { state ->
                handleViewState(state)
            }
            noteViewModel.getAllNotes(idFolder)
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
                showProgressBar(false)
            }

            is NoteState.Error<*> -> {
                showProgressBar(false)
                println("ERROR in ${this.javaClass.simpleName} error: ${state.error}")
            }

            is NoteState.EmptyResult -> {
                showProgressBar(false)
            }
        }
    }

}