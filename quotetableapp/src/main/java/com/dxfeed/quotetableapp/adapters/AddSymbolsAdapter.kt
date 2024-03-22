package com.dxfeed.quotetableapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.R

class AddSymbolsAdapter(private var dataList: List<InstrumentInfo>,
                        private val selectedItems: MutableSet<String>,
                        private val storage: ISaveSymbols) :
    RecyclerView.Adapter<AddSymbolsAdapter.MyViewHolder>() {

    fun updateData(dataList: List<InstrumentInfo>) {
        if (this.dataList != dataList) {
            this.dataList = dataList
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.symbol_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.add_symbol_text_view)
        private val checkBox: CheckBox = itemView.findViewById(R.id.add_symbol_check_box)

        fun bind(item: InstrumentInfo) {
            textView.text = item.symbol + "\n" + item.description
            //should be before setting isChecked. because new value resets previous symbol
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(item.symbol)
                } else {
                    selectedItems.remove(item.symbol)
                }
                storage.save(selectedItems.toList())
            }
            checkBox.isChecked = selectedItems.contains(item.symbol)

            itemView.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }
        }
    }
}
