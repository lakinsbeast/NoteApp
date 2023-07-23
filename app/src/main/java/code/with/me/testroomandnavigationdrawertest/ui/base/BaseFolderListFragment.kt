package code.with.me.testroomandnavigationdrawertest.ui.base

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.safeClickListener
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.databinding.FolderItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentFolderListBinding
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.fragment.FolderHomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

abstract class BaseFolderListFragment :
    BaseFragment<FragmentFolderListBinding>(FragmentFolderListBinding::inflate) {


    lateinit var adapter: BaseAdapter<Folder, FolderItemBinding>
    private lateinit var itemsBinding: FolderItemBinding

    //I had to create this, because notifydatasetchanged did not work when updating an item
    private var listOfFolders = mutableListOf<Folder>()

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    lateinit var folderViewModel: FolderViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemsBinding =
            FolderItemBinding.inflate(layoutInflater, binding.rv.parent as ViewGroup, false)
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@BaseFolderListFragment)
        }

        folderViewModel = ViewModelProvider(this, folderVmFactory)[FolderViewModel::class.java]
        initAdapter()
        initRecyclerView(binding)
    }


    private fun initRecyclerView(binding: FragmentFolderListBinding) {
        binding.apply {
            rv.adapter = adapter
            rv.setHasFixedSize(false)
            rv.layoutManager = LinearLayoutManager(activity)
        }
    }

    fun initAdapter() {
        adapter = object : BaseAdapter<Folder, FolderItemBinding>(itemsBinding) {
            private var selected0 = -1

            init {
                clickListener = {
                    selected0 = it.layoutPosition
                    val item = getItem(it.layoutPosition) as Folder
                    openDetailFragment(item.id)
                }
                onLongClickListener = {
                    selected0 = it.layoutPosition
                    val item = getItem(it.layoutPosition) as Folder
                    val folderHomeFragment = parentFragment as? FolderHomeFragment
                    folderHomeFragment?.navigateToSelectFolderDestintationSheet(item.id)
                }
            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): BaseViewHolder<FolderItemBinding> {
                val binding =
                    FolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                initCreateClickListeners(holder)
                return holder
            }


            override fun onBindViewHolder(
                holder: BaseViewHolder<FolderItemBinding>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)

                holder.binding.apply {
                    val item = getItem(position)
                    titleID.text = item.name
                    initBindClickListeners(holder)
                }
            }

            fun initCreateClickListeners(holder: BaseViewHolder<FolderItemBinding>) {
                holder.itemView.setOnClickListener {
                    clickListener?.invoke(holder)
                }
                holder.itemView.setOnLongClickListener {
                    onLongClickListener?.invoke(holder)
                    return@setOnLongClickListener true
                }
            }

            fun initBindClickListeners(holder: BaseViewHolder<FolderItemBinding>) {
                holder.apply {
                    binding.apply {
                        menuBtn.safeClickListener {
                            val item = getItem(holder.layoutPosition) as Folder
                            val folderHomeFragment = parentFragment as? FolderHomeFragment
                            folderHomeFragment?.navigateToSelectFolderDestintationSheet(item.id)
                        }
                    }
                }
            }
        }
    }

    private fun openDetailFragment(id: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                async {
                    folderViewModel.updateLastOpenedFolder(System.currentTimeMillis(), id)
                }.await()
                withContext(Dispatchers.Main) {
                    val folderHomeFragment = parentFragment as? FolderHomeFragment
                    folderHomeFragment?.navigateToNotesListFragment(id)
                }
            }
        }
    }

}