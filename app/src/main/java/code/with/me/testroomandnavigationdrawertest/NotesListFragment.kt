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
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class NotesListFragment :
    BaseFragment<FragmentNotesListBinding>(FragmentNotesListBinding::inflate) {

    private lateinit var adapter: BaseAdapter<NewNote, NoteItemBinding>
    private lateinit var itemsBinding: NoteItemBinding

    private val NewNotesArray: ArrayList<NewNote> = ArrayList()


    @Inject
    @Named("noteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteViewModel: NoteViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemsBinding = NoteItemBinding.inflate(layoutInflater, binding.rv.parent as ViewGroup, false)

        binding.apply {
            activity?.let {
                val appComponent = (it.application as NotesApplication).appComponent
                appComponent.inject(this@NotesListFragment)
                rv.apply {
//                    scheduleLayoutAnimation()
                    layoutManager = LinearLayoutManager(it)
//                    itemAnimator = null
                }
            }

            fab.setOnClickListener {
                goToAddNoteFragment()
            }
        }

        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        initAdapter()

        initRecyclerView(binding)

        lifecycleScope.launch {
            noteViewModel.getAllNotes().collect { notes ->
                if (notes.isNotEmpty()) {
                    NewNotesArray.clear()
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

    private fun goToAddNoteFragment() {
        findNavController().navigate(NotesListFragmentDirections.actionNotesListFragment2ToAddNoteFragment())
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
                    titleID.text = item.titleNote
                    textID.text = item.textNote
                    posItem.text = posItemText(position)
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
        }
    }

}