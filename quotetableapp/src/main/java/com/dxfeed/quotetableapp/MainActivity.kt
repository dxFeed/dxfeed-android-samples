package com.dxfeed.quotetableapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.event.market.MarketEvent
import com.dxfeed.event.market.Profile
import com.dxfeed.event.market.Quote
import com.dxfeed.quotetableapp.adapters.QuoteAdapter
import com.dxfeed.quotetableapp.adapters.SymbolsDataProvider
import com.dxfeed.quotetableapp.tools.QDQuoteService


class MainActivity : AppCompatActivity() {
    private val symbolsDataProvider = SymbolsDataProvider.getInstance()
    private var symbols = listOf<String>()
    private val eventTypes = listOf(
        Quote::class.java,
        Profile::class.java
    ) as List<Class<out MarketEvent>>

    private val useWebSocket = true

    private val address = if (useWebSocket) "dxlink:wss://demo.dxfeed.com/dxlink-ws" else "demo.dxfeed.com:7300"

    private val service = QDQuoteService(address = address, isWebSocket = useWebSocket)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view);
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuoteAdapter(listOf())

        findViewById<Button>(R.id.editButton).setOnClickListener {
            val intent = Intent(this, EditSymbolsActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.infoButton).setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dxfeed.com/dxfeed-news/"))
            startActivity(browserIntent)
        }

        findViewById<Button>(R.id.addButton).setOnClickListener {
            val intent = Intent(this, AddSymbolsActivity::class.java)
            startActivity(intent)
        }

        symbolsDataProvider.symbols.observe(this) { it ->
            if (symbols == it) {
                return@observe
            }
            symbols = it

            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view);
            val adapter = QuoteAdapter(symbols)
            recyclerView.adapter = adapter
            recyclerView.itemAnimator = null


            service.connect(symbols = symbols,
                eventTypes = eventTypes,
                connectionHandler = {

                },
                eventsHandler = { events ->
                    val positions = events.mapNotNull { event ->
                        when (event) {
                            is Profile -> adapter.update(event)
                            is Quote -> adapter.update(event)
                            else -> { null }
                        }
                    }

                    Handler(Looper.getMainLooper()).post {
                        positions.forEach {
                            adapter.notifyItemChanged(it, null)
                        }
                    }
                })
        }
    }


}