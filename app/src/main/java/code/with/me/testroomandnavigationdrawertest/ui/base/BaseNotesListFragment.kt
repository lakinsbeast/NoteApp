package code.with.me.testroomandnavigationdrawertest.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.NotesListFragmentDirections
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NewNote
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentNotesListBinding
import code.with.me.testroomandnavigationdrawertest.databinding.NoteItemBinding
import code.with.me.testroomandnavigationdrawertest.ui.NoteViewModel
import javax.inject.Inject
import javax.inject.Named

abstract class BaseNotesListFragment : BaseFragment<FragmentNotesListBinding>(
    FragmentNotesListBinding::inflate
) {

    lateinit var adapter: BaseAdapter<NewNote, NoteItemBinding>
    private lateinit var itemsBinding: NoteItemBinding

    var idFolder = -1

    @Inject
    @Named("noteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    lateinit var noteViewModel: NoteViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idFolder = arguments?.getInt("idFolder") ?: 0
        println("idFolder: $idFolder")
        itemsBinding =
            NoteItemBinding.inflate(layoutInflater, binding.rv.parent as ViewGroup, false)

        binding.apply {
            activity?.let {
                val appComponent = (it.application as NotesApplication).appComponent
                appComponent.inject(this@BaseNotesListFragment)
                rv.apply {
                    layoutManager = LinearLayoutManager(it)
                }
            }

            fab.setOnClickListener {
                goToAddNoteFragment()
            }
        }

        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        initAdapter()

        initRecyclerView(binding)
    }

    private fun goToAddNoteFragment() {
        findNavController().navigate(
            NotesListFragmentDirections.actionNotesListFragment2ToAddNoteFragment(
                idFolder
            )
        )
    }

    fun changeUiOnRvUpdate(binding: FragmentNotesListBinding, notes: List<Note>) {
        binding.apply {
            countText.text = "/${notes.size}"
            chipGroupScrollable.gone()
        }
    }

    private fun openDetailFragment(id: Int) {
        findNavController().navigate(
            NotesListFragmentDirections.actionNotesListFragment2ToDetailFragment(
                id
            )
        )
    }

    private fun initRecyclerView(binding: FragmentNotesListBinding) {
        binding.apply {
            rv.adapter = adapter
            rv.setHasFixedSize(false)
        }
    }


    fun initAdapter() {
        adapter = object : BaseAdapter<NewNote, NoteItemBinding>(itemsBinding) {
            private var selected0 = -1

            init {
                clickListener = {
                    selected0 = it.layoutPosition
                    openDetailFragment(selected0)
                }
            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): BaseViewHolder<NoteItemBinding> {
                val binding =
                    NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                holder.itemView.setOnClickListener {
                    clickListener?.invoke(holder)
                }
                return holder
            }


            override fun onBindViewHolder(holder: BaseViewHolder<NoteItemBinding>, position: Int) {
                super.onBindViewHolder(holder, position)

                holder.binding.apply {
                    val item = getItem(position)
                    titleID.text = cutText(item.titleNote)
                    textID.text = cutText(item.textNote)
                    posItem.text = posItemText(position)
                    menuBtn.setOnClickListener {
                        printText()
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

            fun printText() {
                println("bjdsjhfdhfklasd")
            }


            fun posItemText(count: Int): String {
                val count = count + 1
                return if (count < 10) {
                    "0$count/"
                } else {
                    "$count/"
                }
            }
        }
    }

}