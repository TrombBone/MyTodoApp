package com.example.mytodoapp.abstracts

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(private val dataSet: List<T>) :
    RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
        holder.onBind(dataSet[position])

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun <T> onBind(data: T)
    }
}