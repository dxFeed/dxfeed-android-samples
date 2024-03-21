package com.dxfeed.quotetableapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.adapters.ISaveSymbols
import com.dxfeed.quotetableapp.adapters.InstrumentInfo
import com.dxfeed.quotetableapp.adapters.SymbolsDataProvider
import com.dxfeed.quotetableapp.tools.QDIpfService

class MyAdapter(private var dataList: List<InstrumentInfo>,
                private val selectedItems: MutableSet<String>,
                private val storage: ISaveSymbols) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

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
            checkBox.isChecked = selectedItems.contains(item.symbol)
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedItems.add(item.symbol)
                } else {
                    selectedItems.remove(item.symbol)
                }
                storage.save(selectedItems.toList())
            }
            itemView.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }
        }
    }
}

class AddSymbolsActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private val symbolsDataProvider = QuoteApp.dataProvider

    private val service = QDIpfService(address = "https://demo:demo@tools.dxfeed.com/ipf?TYPE=FOREX,STOCK&compression=zip")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_symbols_activity)

        recyclerView = findViewById(R.id.add_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        service.connect { loaded ->
            Handler(Looper.getMainLooper()).post {
                val dataList = loaded.map {
                    InstrumentInfo(it.symbol, it.description)
                }
                symbolsDataProvider.allSymbols = dataList.toTypedArray()
                adapter.updateData(dataList)
            }
        }
        adapter = MyAdapter(
            symbolsDataProvider.allSymbols.toList(),
            symbolsDataProvider.selectedSymbols.toMutableSet(),
            symbolsDataProvider
        )
        recyclerView.adapter = adapter
    }
}