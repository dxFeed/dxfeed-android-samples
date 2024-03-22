package com.dxfeed.quotetableapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.event.market.MarketEvent
import com.dxfeed.event.market.Profile
import com.dxfeed.event.market.Quote
import com.dxfeed.quotetableapp.adapters.QuoteAdapter
import com.dxfeed.quotetableapp.adapters.SymbolsDataProvider
import com.dxfeed.quotetableapp.extensions.stringValue
import com.dxfeed.quotetableapp.tools.QDQuoteService

class MainActivity : AppCompatActivity() {
    private val symbolsDataProvider = SymbolsDataProvider.getInstance()
    private var symbols = listOf<String>()
    private val eventTypes = listOf(
        Quote::class.java,
        Profile::class.java
    ) as List<Class<out MarketEvent>>

    private val useWebSocket = false
    private val address = if (useWebSocket) "dxlink:wss://demo.dxfeed.com/dxlink-ws" else "demo.dxfeed.com:7300"

    private val service = QDQuoteService(address = address, isWebSocket = useWebSocket)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view);
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuoteAdapter(listOf())
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val buttonClick = findViewById<ImageButton>(R.id.editButton)
        buttonClick.setOnClickListener {
            val intent = Intent(this, EditSymbolsActivity::class.java)
            startActivity(intent)
        }
        symbolsDataProvider.data.observe(this) {
            if (symbols == it) {
                return@observe
            }
            symbols = it

            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view);
            val adapter = QuoteAdapter(symbols)
            recyclerView.adapter = adapter

            service.connect(symbols = symbols,
                eventTypes = eventTypes,
                connectionHandler = {
                    Handler(Looper.getMainLooper()).post {
                        val connectionTextView = findViewById<TextView>(R.id.connectionTextView);
                        connectionTextView.text = it.stringValue(this)
                    }
                },
                eventsHandler = { events ->
                    events.forEach {
                        when (it) {
                            is Profile -> adapter.update(it)
                            is Quote -> adapter.update(it)
                        }
                    }
                    Handler(Looper.getMainLooper()).post {
                        adapter.notifyDataSetChanged()
                    }
                })
        }
    }


}