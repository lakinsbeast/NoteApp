package code.with.me.testroomandnavigationdrawertest.ui

import androidx.recyclerview.widget.DiffUtil
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Identifiable

class DiffUtil<T : Identifiable> : DiffUtil.ItemCallback<T>() {

    //Раньше было oldItemPosition.id == newItemPosition.id() но это вызывало проблемы, потому что в новом листе оставались старые элементы
    override fun areItemsTheSame(oldItemPosition: T, newItemPosition: T): Boolean =
        oldItemPosition.hashCode() == newItemPosition.hashCode()

    override fun areContentsTheSame(oldItemPosition: T, newItemPosition: T): Boolean =
        oldItemPosition.hashCode() == newItemPosition.hashCode()
}