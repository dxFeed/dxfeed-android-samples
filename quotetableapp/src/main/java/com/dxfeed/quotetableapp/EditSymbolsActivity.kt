package com.dxfeed.quotetableapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.adapters.SymbolsDataProvider

class EditSymbolsAdapter() : RecyclerView.Adapter<EditSymbolsAdapter.ViewHolder>() {
    private val symbolsDataProvider = SymbolsDataProvider(QuoteApp.context!!)

    private val items: MutableList<String> = symbolsDataProvider.selectedSymbols.toMutableList()

    fun getSymbols(): Array<String> = items.toTypedArray()

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

class EditSymbolsActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EditSymbolsAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit_quotes_activity)

        recyclerView = findViewById(R.id.edit_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EditSymbolsAdapter()

        recyclerView.adapter = adapter

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.swapItems(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.removeItem(position)
            }
        }

        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}