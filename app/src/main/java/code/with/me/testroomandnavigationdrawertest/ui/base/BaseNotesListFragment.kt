package code.with.me.testroomandnavigationdrawertest.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentNotesListBinding
import code.with.me.testroomandnavigationdrawertest.databinding.NoteItemBinding
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MainScreenFragment
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModel
import javax.inject.Inject
import javax.inject.Named


abstract class BaseNotesListFragment : BaseFragment<FragmentNotesListBinding>(
    FragmentNotesListBinding::inflate
) {
    lateinit var adapter: BaseAdapter<Note, NoteItemBinding>
    private lateinit var itemsBinding: NoteItemBinding

    var idFolder = -1

    @Inject
    @Named("noteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    lateinit var noteViewModel: NoteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initAdapterBinding()
        initAppComponent()
        initAdapter()
        initRecyclerView(binding)
    }


    private fun initAppComponent() {
        val appComponent = (activity?.application as NotesApplication).appComponent
        appComponent.inject(this@BaseNotesListFragment)
    }

    private fun initAdapterBinding() {
        itemsBinding =
            NoteItemBinding.inflate(layoutInflater, binding.rv.parent as ViewGroup, false)
    }

    private fun initViewModel() {
        noteViewModel = ViewModelProvider(requireActivity(), factory)[NoteViewModel::class.java]
    }

    private fun initRecyclerView(binding: FragmentNotesListBinding) {
        binding.apply {
            rv.adapter = adapter
            rv.setHasFixedSize(false)
            rv.layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun initAdapter() {
        adapter = object : BaseAdapter<Note, NoteItemBinding>(itemsBinding) {
            private var selected0 = -1

            init {
                clickListener = {
                    selected0 = it.layoutPosition
                    val item = getItem(it.layoutPosition) as Note
                    openDetailFragment(item.second_id)
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
                    val item = getItem(holder.adapterPosition)
                    titleID.text = cutText(item.titleNote).checkEmptyTitle()
                    textID.text = cutText(item.textNote).checkEmptyText()
                    menuBtn.setOnClickListener {
                        printText()
                    }
                }
            }

            private fun openDetailFragment(id: Int) {
//                val noteHomeFragment =
//                    activity?.supportFragmentManager?.findFragmentByTag("NotesListFragment")
                println("before")
                for (fragment in activity?.supportFragmentManager?.fragments!!) {
                    println("fragment: ${fragment.javaClass.simpleName}")
                    if (fragment is MainScreenFragment) {
                        fragment.navigateToViewANoteSheet(id)
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

            fun String.checkEmptyTitle(): String {
                if (this.isEmpty()) {
                    return "Пустая заметка"
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