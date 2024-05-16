package com.example.mytodoapp.components.abstracts

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder?>(callback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, VH>(callback) {

    override fun onBindViewHolder(holder: VH & Any, position: Int) {
        val currentItem = getItem(position)
        (holder as BaseViewHolder).onBind(currentItem)
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun <T> onBind(item: T)

    }
}