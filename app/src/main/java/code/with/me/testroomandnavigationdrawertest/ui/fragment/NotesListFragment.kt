package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.enums.NoteItemsCallback
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentNotesListBinding
import code.with.me.testroomandnavigationdrawertest.databinding.NoteItemBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.supportFragmentManager
import code.with.me.testroomandnavigationdrawertest.ui.dialog.PreviewNoteDialog
import code.with.me.testroomandnavigationdrawertest.ui.sheet.NoteMenuSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NotesListUserAction
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class NotesListFragment : BaseFragment<FragmentNotesListBinding>(
    FragmentNotesListBinding::inflate,
) {
    lateinit var adapter: BaseAdapter<Note, NoteItemBinding>
    private lateinit var itemsBinding: NoteItemBinding

//    @Inject
//    @Named("noteVMFactory")
//    lateinit var factory: ViewModelProvider.Factory
    private val noteViewModel: NoteViewModel by viewModels()
//    lazy {
//        ViewModelProvider(this, factory)[NoteViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** достаем айди папки, если он был выбран и выводим все заметки, которые
         * привязаны к папке*/
        noteViewModel.idFolder = arguments?.getLong("idFolder") ?: -1
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterBinding()

        initAdapter()
        initRecyclerView(binding)
        listenVMObservers()
    }

    private fun listenVMObservers() {
        lifecycleScope.launch {
            noteViewModel.state.observe(viewLifecycleOwner) { state ->
                handleViewState(state)
            }
            noteViewModel.userActionState.observe(viewLifecycleOwner) { state ->
                handleUserActionState(state)
            }
            if (noteViewModel.idFolder == -1L) {
                noteViewModel.getAllNotes()
            } else {
                noteViewModel.getAllNotes(noteViewModel.idFolder)
            }
        }
    }

    // TODO: проверить нужно или нет
    private fun handleUserActionState(state: NotesListUserAction) {
        binding.apply {
            when (state) {
                is NotesListUserAction.ShareText<*> -> {
                    if (state.data.toString().isNotBlank()) {
                        Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, state.data.toString())
                            startActivity(Intent.createChooser(this, "Share via"))
                        }
                    } else {
                        Toast.makeText(context, "Пустой текст", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.isGone = !show
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

    private fun initAdapterBinding() {
        itemsBinding =
            NoteItemBinding.inflate(layoutInflater, binding.rv.parent as ViewGroup, false)
    }

    private fun initRecyclerView(binding: FragmentNotesListBinding) {
        binding.apply {
            rv.adapter = adapter
            rv.setHasFixedSize(false)
            rv.layoutManager = LinearLayoutManager(requireActivity())
            rv.animation =
                AnimationUtils.loadAnimation(
                    activity(),
                    R.anim.translate_slide_recycler_anim,
                )
        }
    }

    private fun initAdapter() {
        adapter =
            object : BaseAdapter<Note, NoteItemBinding>(itemsBinding) {
                init {
                    clickListener = {
                        val item = getItem(it.layoutPosition) as Note
                        openDetailFragment(it.itemView, item.id)
                    }
                    onLongClickListener = {
                        val item = getItem(it.layoutPosition) as Note

                        openPreviewDialog(item.id /*x, y*/)
                    }
                }

                private fun openPreviewDialog(id: Long) {
                    val bundle = Bundle()
                    val dialog = PreviewNoteDialog()
                    dialog.arguments =
                        bundle.apply {
                            putLong("noteId", id)
                        }

                    dialog.show(activity().supportFragmentManager(), "CreateFolderDialog")
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int,
                ): BaseViewHolder<NoteItemBinding> {
                    val binding =
                        NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    val holder = BaseViewHolder(binding)
                    holder.itemView.setOnClickListener {
                        clickListener.invoke(holder)
                    }
                    holder.itemView.setOnLongClickListener {
                        onLongClickListener.invoke(holder)
                        true
                    }

                    return holder
                }

                override fun onBindViewHolder(
                    holder: BaseViewHolder<NoteItemBinding>,
                    position: Int,
                ) {
                    super.onBindViewHolder(holder, position)
                    holder.binding.apply {
                        val item = getItem(holder.adapterPosition)
                        titleID.text = cutText(item.titleNote).checkEmptyTitle()
                        textID.text = cutText(item.textNote).checkEmptyText()
                        menuBtn.setOnClickListener {
                            val sheet =
                                NoteMenuSheet(item.id) {
                                    when (it) {
                                        NoteItemsCallback.SHARE -> {
                                            noteViewModel.shareTextNote(item.id)
                                        }

                                        NoteItemsCallback.MOVE -> {
                                            println("MOVE")
                                        }

                                        NoteItemsCallback.FAVORITE -> {
                                            noteViewModel.setToFavorite(item.id)
                                        }

                                        NoteItemsCallback.LOCK -> {
                                            println("LOCK")
                                        }

                                        NoteItemsCallback.DELETE -> {
                                            noteViewModel.delete(item)
                                        }
                                    }
                                }
                            sheet.isDraggable = false
                            activity().sheetController.showSheet(activity(), sheet)
                        }
                    }
                }

                private fun openDetailFragment(
                    id1: View,
                    id: Long,
                ) {
                    for (fragment in activity?.supportFragmentManager?.fragments!!) {
                        println("fragment: ${fragment.javaClass.simpleName}")
                        if (fragment is MainScreenFragment) {
                            fragment.openMakeNoteFragment(id)
                            break
                        }
                    }
                }

                fun cutText(text: String): String {
                    return if (text.length > 25) {
                        text.substring(0, 25) + "..."
                    } else {
                        text
                    }
                }

                fun posItemText(count: Int): String {
                    val count = count + 1
                    return if (count < 10) {
                        "0$count/"
                    } else {
                        "$count/"
                    }
                }

                fun String.checkEmptyTitle(): String {
                    if (this.isEmpty()) {
                        return "Без названия"
                    }
                    return this
                }

                fun String.checkEmptyText(): String {
                    if (this.isEmpty()) {
                        return "Нет дополнительного текста"
                    }
                    return this
                }
            }
    }
}
