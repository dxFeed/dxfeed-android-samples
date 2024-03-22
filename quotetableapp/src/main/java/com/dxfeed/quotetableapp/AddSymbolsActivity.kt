package com.dxfeed.quotetableapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.adapters.InstrumentInfo
import com.dxfeed.quotetableapp.adapters.AddSymbolsAdapter
import com.dxfeed.quotetableapp.adapters.SymbolsDataProvider
import com.dxfeed.quotetableapp.tools.QDIpfService

class AddSymbolsActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddSymbolsAdapter
    private val symbolsDataProvider = SymbolsDataProvider.getInstance()

    private val service = QDIpfService(address = "https://demo:demo@tools.dxfeed.com/ipf?TYPE=FOREX,STOCK&compression=zip")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_symbols_activity)

        recyclerView = findViewById(R.id.add_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AddSymbolsAdapter(
            symbolsDataProvider.allSymbols.toList(),
            symbolsDataProvider.selectedSymbols.toMutableSet(),
            symbolsDataProvider
        )
        recyclerView.adapter = adapter
        service.connect { loaded ->
            Handler(Looper.getMainLooper()).post {
                val dataList = loaded.map {
                    InstrumentInfo(it.symbol, it.description)
                }.sortedBy { it.symbol }

                symbolsDataProvider.allSymbols = dataList.toTypedArray()
                adapter.updateData(dataList)
            }
        }
    }
}