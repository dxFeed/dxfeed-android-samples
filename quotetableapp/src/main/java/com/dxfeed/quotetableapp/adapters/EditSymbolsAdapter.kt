package com.dxfeed.quotetableapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.R

class EditSymbolsAdapter(val symbolsDataProvider: SymbolsDataProvider) : RecyclerView.Adapter<EditSymbolsAdapter.ViewHolder>() {

    private var items: MutableList<String> = symbolsDataProvider.selectedSymbols.toMutableList()

    fun updateData(dataList: List<String>) {
        items = dataList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        symbolsDataProvider.selectedSymbols = items.toTypedArray()
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        val temp = items[fromPosition]
        items[fromPosition] = items[toPosition]
        items[toPosition] = temp
        notifyItemMoved(fromPosition, toPosition)
        symbolsDataProvider.selectedSymbols = items.toTypedArray()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById<TextView?>(android.R.id.text1).apply {
            this.setTextColor(
                ContextCompat.getColor(context, R.color.white)
            )
        }

        fun bind(item: String) {
            textView.text = item
        }
    }
}