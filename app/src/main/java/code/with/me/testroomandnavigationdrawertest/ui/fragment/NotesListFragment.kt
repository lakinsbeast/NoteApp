package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseNotesListFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SeeTextSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionNote
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class NotesListFragment :
    BaseNotesListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idFolder = arguments?.getInt("idFolder") ?: -1
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
        activity().sheetController.showSheet(
            activity(), SeeTextSheet( // "link(http://example.com) \n" +
                "[link](https://example.com) \n" +
                        "# Заголовок 1\n" +
                        "\n" +
                        "Это **жирный текст** и *курсив*.\n" +
                        "Это *не рабочий жирный текст** и *неправильный курсив**.\n" +
                        "Это **жирный текст** и ***жирный курсив***.\n" +
                        "> > аволпвао о ывалдо ыдвлоа ылд о\n" +
                        "[link](http://example.com) \n" +
                        "\n" +
                        "# Заголовок 1\n" +
                        "\n" +
                        "## Заголовок 2\n" +
                        "\n" +
                        "### Заголовок 3\n" +
                        "\n" +
                        "#### Заголовок 4\n" +
                        "\n" +
                        "##### Заголовок 5\n" +
                        "\n" +
                        "###### Заголовок 6\n" +
                        "\n" +
                        "~~Зачеркнутый текст~~ 2\n" +
                        "\n" +
                        "Список:\n" +
                        "\n" +
                        "1. Пункт 1\n" +
                        "2. Пункт 2\n" +
                        "   - Подпункт 2.1\n" +
                        "   - Подпункт 2.2\n"
            )
        )
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
                adapter.submitList(newNoteList.toMutableList())
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