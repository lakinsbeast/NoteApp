package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteFTS
import code.with.me.testroomandnavigationdrawertest.data.utils.hideKeyboard
import code.with.me.testroomandnavigationdrawertest.databinding.NoteItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.SearchFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.supportFragmentManager
import code.with.me.testroomandnavigationdrawertest.ui.dialog.PreviewNoteDialog
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : BaseFragment<SearchFragmentBinding>(
    SearchFragmentBinding::inflate,
) {
    private lateinit var adapter: BaseAdapter<NoteFTS, NoteItemBinding>
    private lateinit var itemsBinding: NoteItemBinding

//    @Inject
//    @Named("searchVMFactory")
//    lateinit var factory: ViewModelProvider.Factory
    private val searchViewModel: SearchViewModel by viewModels()
//    lazy {
//        ViewModelProvider(this, factory)[SearchViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterBinding()
        initAdapter()
        initRecyclerView()
        listenViewModel()
        setSearchEditTextStyle()
        hideCloseButtonInSearchView()
        configureSearchViewForExpandedInput()
        binding.searchView.clearFocus()
        setTouchListeners()
        setClickListeners()
    }

    private fun configureSearchViewForExpandedInput() {
        // включаем кликабельность searchview по всей searchview, а не только по иконке
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
    }

    private fun setClickListeners() {
        binding.backArrow.setOnClickListener {
            closeFragment()
        }
    }

    /** убирает кнопку "крестик" справа от searchview*/
    private fun hideCloseButtonInSearchView() {
        binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).apply {
            isEnabled = false
            setImageDrawable(null)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListeners() {
        // убираем фокус при клике на любом месте, чтоб не пришлось нажимать два раза back button
        binding.rv.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.searchView.clearFocus()
            true
        }
        binding.root.setOnTouchListener { _, _ ->
            binding.searchView.clearFocus()
            true
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                searchViewModel.noteList.collect { noteList ->
                    adapter.submitList(noteList.toMutableList())
                }
            }
        }
    }

    /** применяем стиль для searchView*/
    private fun setSearchEditTextStyle() {
        val searchEditText =
            binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(resources.getColor(R.color.white, null))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            searchEditText.textCursorDrawable =
                ResourcesCompat.getDrawable(resources, R.drawable.text_cursor, null)
        }
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
                init {
                    clickListener = {
                        val item = getItem(it.layoutPosition) as NoteFTS
                        openDetailFragment(item.id)
                        hideKeyboard()
                        closeFragment()
                    }
                    onLongClickListener = {
                        val item = getItem(it.layoutPosition) as NoteFTS
                        openPreviewDialog(item.id)
                        hideKeyboard()
                        closeFragment()
                    }
                }

                private fun closeFragment() {
                    this@SearchFragment.binding.searchView.clearFocus()
                    this@SearchFragment.closeFragment()
                }

                private fun openPreviewDialog(id: Long) {
                    val bundle = Bundle()
                    PreviewNoteDialog().apply {
                        arguments = bundle.apply { putLong("noteId", id) }
                        this.show(activity().supportFragmentManager(), "CreateFolderDialog")
                    }
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
                        menuBtn.isGone = true
                    }
                }

                fun openDetailFragment(id: Long) {
                    for (fragment in activity?.supportFragmentManager?.fragments!!) {
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
