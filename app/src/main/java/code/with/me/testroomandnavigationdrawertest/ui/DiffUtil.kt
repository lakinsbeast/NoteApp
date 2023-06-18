package code.with.me.testroomandnavigationdrawertest.ui

import androidx.recyclerview.widget.DiffUtil
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Model

class DiffUtil<T: Model>: DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItemPosition: T, newItemPosition: T): Boolean = oldItemPosition.id == newItemPosition.id

    override fun areContentsTheSame(oldItemPosition: T, newItemPosition: T): Boolean = oldItemPosition.id == newItemPosition.id
}