package code.with.me.testroomandnavigationdrawertest.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewbinding.ViewBindings
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Model

open class BaseAdapter<T : Model, A: ViewBinding>(val binding: A): ListAdapter<T, BaseAdapter.BaseViewHolder<A>>(DiffUtil()) {

    var recycView: RecyclerView? = null
    var clickListener: ((binding: BaseViewHolder<A>) -> Unit)? = null
    var onLongClickListener: ((binding: BaseViewHolder<A>) -> Unit)? = null

    class BaseViewHolder<T : ViewBinding>(val binding: T) :
        RecyclerView.ViewHolder(binding.root) {

        }

    override fun submitList(list: MutableList<T>?) {
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<A> {
        var holder = BaseViewHolder(binding)
        holder.itemView.setOnClickListener {
            clickListener?.invoke(holder)
        }
        onLongClickListener?.let { l ->
            holder.itemView.setOnLongClickListener {
                l.invoke(holder)
                return@setOnLongClickListener true
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<A>, position: Int) {

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recycView = null
    }

}
