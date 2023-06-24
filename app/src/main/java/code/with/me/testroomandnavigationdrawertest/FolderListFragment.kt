package code.with.me.testroomandnavigationdrawertest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.databinding.FolderItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentFolderListBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddFolderSheet
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class FolderListFragment :
    BaseFragment<FragmentFolderListBinding>(FragmentFolderListBinding::inflate) {
    private lateinit var adapter: BaseAdapter<Folder, FolderItemBinding>
    private lateinit var itemsBinding: FolderItemBinding

    //I had to create this, because notifydatasetchanged did not work when updating an item
    private var listOfFolders = mutableListOf<Folder>()

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    private lateinit var folderViewModel: FolderViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemsBinding =
            FolderItemBinding.inflate(layoutInflater, binding.rv.parent as ViewGroup, false)
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@FolderListFragment)
        }



        folderViewModel = ViewModelProvider(this, folderVmFactory)[FolderViewModel::class.java]
        initAdapter()
        initRecyclerView(binding)
//        val sheet = AddFolderSheet()

        binding.apply {
            addFolderBtn.setOnClickListener {
//                sheet.showSheet(parentFragmentManager)
                findNavController().navigate(FolderListFragmentDirections.actionFolderListFragmentToAddFolderSheet())
            }
        }

        lifecycleScope.launch {
//            folderViewModel.insertFolder(Folder("FirstFolder"))
            folderViewModel.getAllFolders().collect() {
                println("getAllFolders = $it")
                listOfFolders.clear()
                listOfFolders = it.toMutableList()
                adapter.submitList(listOfFolders)
                adapter.notifyDataSetChanged()
            }
        }
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
                    openDetailFragment(selected0)
                }
                onLongClickListener = {
                    selected0 = it.layoutPosition
                    val item = getItem(it.layoutPosition)
                    findNavController().navigate(
                        FolderListFragmentDirections.actionFolderListFragmentToSelectFolderDestinationSheet(
                            item.id
                        )
                    )
                }
            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): BaseViewHolder<FolderItemBinding> {
                val binding =
                    FolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                holder.itemView.setOnClickListener {
                    clickListener?.invoke(holder)
                }
                holder.itemView.setOnLongClickListener {
                    onLongClickListener?.invoke(holder)
                    return@setOnLongClickListener true
                }
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
                    textID.text = item.id.toString()
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

    private fun openDetailFragment(id: Int) {
        findNavController().navigate(R.id.action_folderListFragment_to_notesListFragment2)
    }

}