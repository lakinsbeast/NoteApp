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
import code.with.me.testroomandnavigationdrawertest.databinding.DbItemsBinding
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentNotesListBinding
import code.with.me.testroomandnavigationdrawertest.ui.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.NoteViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotesListFragment :
    BaseFragment<FragmentNotesListBinding>(FragmentNotesListBinding::inflate) {

    private lateinit var adapter: BaseAdapter<NewNote, DbItemsBinding>
    private lateinit var itemsBinding: DbItemsBinding

    private val NewNotesArray: ArrayList<NewNote> = ArrayList()


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteViewModel: NoteViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemsBinding = DbItemsBinding.inflate(layoutInflater, binding.rv.parent as ViewGroup, false)

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

        }

        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
        initAdapter()

        initRecyclerView(binding)

        lifecycleScope.launch {
            noteViewModel.getAllNotes().collect {
                if (it.isNotEmpty()) {
                    NewNotesArray.clear()
                    it.forEach {
                        NewNotesArray.add(it.toNewNote())
                    }
                    adapter.submitList(NewNotesArray as MutableList<NewNote>)
                    binding.rv.adapter?.notifyDataSetChanged()
                } else {
                    binding.apply {
//                        welcomeLayout.visibility = View.VISIBLE
                    }
                }
                changeUiOnRvUpdate(binding, it)
            }
        }
    }

    fun changeUiOnRvUpdate(binding: FragmentNotesListBinding, notes: List<Note>) {
        binding.apply {
            countText.text = "/${notes.size}"
            chipGroup.gone()
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
        adapter = object : BaseAdapter<NewNote, DbItemsBinding>(itemsBinding) {
            private var selected0 = -1

            init {
                clickListener = {
                    selected0 = it.layoutPosition
                    openDetailFragment(selected0)
                }
            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): BaseViewHolder<DbItemsBinding> {
                val binding =
                    DbItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                holder.itemView.setOnClickListener {
                    clickListener?.invoke(holder)
                }
                return holder
            }


            override fun onBindViewHolder(holder: BaseViewHolder<DbItemsBinding>, position: Int) {
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