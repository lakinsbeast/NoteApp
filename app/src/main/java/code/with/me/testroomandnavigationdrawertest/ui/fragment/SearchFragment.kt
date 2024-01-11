package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.Utils.hideKeyboard
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteFTS
import code.with.me.testroomandnavigationdrawertest.databinding.NoteItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.SearchFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.dialog.PreviewNoteDialog
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SearchViewModel
import javax.inject.Inject
import javax.inject.Named

class SearchFragment : BaseFragment<SearchFragmentBinding>(
    SearchFragmentBinding::inflate,
) {
    lateinit var adapter: BaseAdapter<NoteFTS, NoteItemBinding>
    private lateinit var itemsBinding: NoteItemBinding

    @Inject
    @Named("searchVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterBinding()
        initAdapter()
        initRecyclerView()
        searchViewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
        searchViewModel.noteList.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }
        setSearchEditTextStyle()

        val closeBtn =
            binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeBtn.isEnabled = false
        closeBtn.setImageDrawable(null)

        // включаем кликабельность ssearchview по всей searchview, а не только по иконке
        binding.searchView.isIconified = false

        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    println("onQueryTextSubmit: $query")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    println("onQueryTextChange: $newText")
                    searchViewModel.search(newText ?: "")
                    return false
                }
            },
        )

        binding.searchView.clearFocus()

        // убираем фокус при клике на любом месте, чтоб не пришлось нажимать два раза back button
        binding.rv.setOnTouchListener { v, event ->
            hideKeyboard()
            binding.searchView.clearFocus()
            true
        }
        binding.root.setOnTouchListener { v, event ->
            binding.searchView.clearFocus()
            true
        }

        binding.backArrow.setOnClickListener {
            closeFragment()
        }
    }

    private fun setSearchEditTextStyle() {
        val searchEditText =
            binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(resources.getColor(R.color.white, null))
        searchEditText.textCursorDrawable =
            resources.getDrawable(R.drawable.text_cursor, null)
    }

    private fun initAdapterBinding() {
        itemsBinding =
            NoteItemBinding.inflate(layoutInflater, binding.rv.parent as ViewGroup, false)
    }

    private fun initRecyclerView() {
        binding.apply {
            rv.adapter = adapter
            rv.setHasFixedSize(false)
            rv.layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun initAdapter() {
        adapter =
            object : BaseAdapter<NoteFTS, NoteItemBinding>(itemsBinding) {
                private var selected0 = -1

                init {
                    clickListener = {
                        selected0 = it.layoutPosition
                        val item = getItem(it.layoutPosition) as NoteFTS
                        openDetailFragment(item.id)
                        hideKeyboard()
                        this@SearchFragment.binding.searchView.clearFocus()
                        this@SearchFragment.closeFragment()
                    }
                    onLongClickListener = {
                        selected0 = it.layoutPosition
                        val item = getItem(it.layoutPosition) as NoteFTS
                        openPreviewDialog(item.id)
                        hideKeyboard()
                        this@SearchFragment.binding.searchView.clearFocus()
                        this@SearchFragment.closeFragment()
                    }
                }

                private fun openPreviewDialog(id: Long) {
                    val bundle = Bundle()
                    val dialog = PreviewNoteDialog()
                    dialog.arguments =
                        bundle.apply {
                            putLong("noteId", id)
                        }

                    dialog.show(activity().supportFragmentManager, "CreateFolderDialog")
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int,
                ): BaseViewHolder<NoteItemBinding> {
                    val binding =
                        NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    val holder = BaseViewHolder(binding)
                    holder.itemView.setOnClickListener {
                        clickListener?.invoke(holder)
                    }
                    holder.itemView.setOnLongClickListener {
                        onLongClickListener?.invoke(holder)
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
                        menuBtn.isGone = true
                    }
                }

                fun openDetailFragment(id: Long) {
                    for (fragment in activity?.supportFragmentManager?.fragments!!) {
                        println("fragment: ${fragment.javaClass.simpleName}")
                        if (fragment is MainScreenFragment) {
//                        fragment.navigateToViewANoteSheet(id)
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

                fun String.checkEmptyTitle(): String {
                    if (this.isEmpty()) {
                        return "Без названия"
                    }
                    return this
                }

                fun posItemText(count: Int): String {
                    val count = count + 1
                    return if (count < 10) {
                        "0$count/"
                    } else {
                        "$count/"
                    }
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
