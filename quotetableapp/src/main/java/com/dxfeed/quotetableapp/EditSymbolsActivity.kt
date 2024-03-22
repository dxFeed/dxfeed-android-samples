package com.dxfeed.quotetableapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.adapters.EditSymbolsAdapter
import com.dxfeed.quotetableapp.adapters.SymbolsDataProvider

class EditSymbolsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EditSymbolsAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val symbolsDataProvider = SymbolsDataProvider.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit_quotes_activity)
        val buttonClick = findViewById<Button>(R.id.add_button)
        buttonClick.setOnClickListener {
            val intent = Intent(this, AddSymbolsActivity::class.java)
            startActivity(intent)
        }
        recyclerView = findViewById(R.id.edit_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EditSymbolsAdapter(symbolsDataProvider.selectedSymbols.toList())

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
        symbolsDataProvider.data.observe(this) {
            adapter.updateData(it)
        }
    }

    override fun onPause() {
        super.onPause()
        symbolsDataProvider.selectedSymbols = adapter.items.toTypedArray()
    }
}