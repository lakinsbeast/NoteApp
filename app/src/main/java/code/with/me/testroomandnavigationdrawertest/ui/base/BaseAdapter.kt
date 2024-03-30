package code.with.me.testroomandnavigationdrawertest.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Identifiable

open class BaseAdapter<T : Identifiable, A : ViewBinding>(val binding: A) :
    ListAdapter<T, BaseAdapter.BaseViewHolder<A>>(
        code.with.me.testroomandnavigationdrawertest.ui.DiffUtil(),
    ) {
    private var recyclerView: RecyclerView? = null

    // cannot be used
    //    val newclickListener by lazy {
//        { binding: BaseViewHolder<A> ->
//
//        }
//    }

    lateinit var clickListener: (binding: BaseViewHolder<A>) -> Unit
    lateinit var onLongClickListener: (binding: BaseViewHolder<A>) -> Unit

    class BaseViewHolder<T : ViewBinding>(val binding: T) :
        RecyclerView.ViewHolder(binding.root)

    override fun submitList(list: MutableList<T>?) {
        super.submitList(list)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<A> {
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<A>,
        position: Int,
    ) {
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }
}
