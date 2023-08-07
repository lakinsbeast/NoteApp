package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.Utils.getDate
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.ViewNoteDetailSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject
import javax.inject.Named

class ViewANoteSheet : BaseSheet<ViewNoteDetailSheetBinding>(ViewNoteDetailSheetBinding::inflate) {

    @Inject
    @Named("noteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteViewModel: NoteViewModel

    lateinit var adapter: BaseAdapter<PhotoModel, PhotoItemBinding>
    private lateinit var photoItem: PhotoItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
        setFullScreenSheet()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterInflating()
        initAdapter()
        initViewModel()
    }

    private fun initAdapterInflating() {
        photoItem =
            PhotoItemBinding.inflate(layoutInflater, binding.photoList.parent as ViewGroup, false)
    }

    private fun initViewModel() {
        val idIntent = arguments?.getInt("noteId") ?: 0
        noteViewModel.getNoteById(idIntent)
        noteViewModel.state.observe(viewLifecycleOwner) { state ->
            handleViewState(state)
        }

    }

    private fun handleViewState(state: NoteState) {
        when (state) {
            is NoteState.Loading -> {
                showProgressBar(true)
            }

            is NoteState.Result<*> -> {
                binding.apply {
                    val note = state.data as Note
                    println("note: ${state.data}")
                    dateText.text = getDate(note.lastTimestampCreate)
                    titleText.text = note.titleNote
                    text.text = note.textNote
                    adapter.submitList(ArrayList(note.listOfImages))
                    adapter.notifyDataSetChanged()
                }
                showProgressBar(false)
            }

            is NoteState.Error<*> -> {
                showProgressBar(false)
                dialog?.window?.decorView?.let { window ->
                    Snackbar.make(
                        window, //binding.root is not work :(
                        state.error.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                println("ERROR in ${this.javaClass.simpleName} error: ${state.error}")
            }

            is NoteState.EmptyResult -> {
                showProgressBar(false)
            }
        }
    }

    private fun initAdapter() {
        adapter = object : BaseAdapter<PhotoModel, PhotoItemBinding>(photoItem) {
            private var selected0 = -1

            init {
                clickListener = {
                    selected0 = it.layoutPosition
                    val item = getItem(it.layoutPosition) as PhotoModel
//                    openDetailFragment(item.id)
                }
                onLongClickListener = {
                    selected0 = it.layoutPosition
                    val item = getItem(it.layoutPosition) as PhotoModel
                }
            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): BaseViewHolder<PhotoItemBinding> {
                val binding =
                    PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                //Почему-то pinch to zoom не работает даже с библиотеками
//                holder.itemView.setOnTouchListener(context?.let { ImageMatrixTouchHandler(it) })
                holder.itemView.setOnLongClickListener {
                    onLongClickListener?.invoke(holder)
                    return@setOnLongClickListener true
                }
                return holder
            }


            override fun onBindViewHolder(
                holder: BaseViewHolder<PhotoItemBinding>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)

                holder.binding.apply {
                    val item = getItem(position)
                    Glide.with(this@ViewANoteSheet).load(item.path).into(image)
                }
            }
        }.apply {
            this@ViewANoteSheet.binding.photoList.adapter = this
            this@ViewANoteSheet.binding.photoList.apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }


}