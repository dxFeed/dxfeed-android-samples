package com.dxfeed.latencytestapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.api.DXEndpoint
import com.dxfeed.event.market.MarketEvent
import com.dxfeed.event.market.Quote
import com.dxfeed.latencytestapp.adapters.QuoteAdapter
import com.dxfeed.latencytestapp.extensions.stringValue
import com.dxfeed.latencytestapp.tools.QDService
import com.dxfeed.latencytestapp.tools.Speedometer

class MainActivity : AppCompatActivity() {
    private val symbols = listOf(
    "ETH/USD:GDAX"
    )
    private val eventTypes = listOf(Quote::class.java)

    private val adapter = QuoteAdapter(symbols)
    private val speedometer = Speedometer(2000) {
        Handler(Looper.getMainLooper()).post {
            adapter.reload(it)
        }
    }
    private val service = QDService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view);
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        findViewById<Button>(R.id.connect_button)
            .setOnClickListener {
                if (service.state == DXEndpoint.State.CONNECTED) {
                    service.disconnect()
                } else {
                    service.connect(findViewById<EditText>(R.id.address_text_view).text.toString(), symbols, eventTypes, connectionHandler = {
                        Handler(Looper.getMainLooper()).post {
                            val connectionTextView = findViewById<TextView>(R.id.connectionTextView);
                            connectionTextView.text = it.stringValue(this)
                            findViewById<Button>(R.id.connect_button).text  =
                                if (it == DXEndpoint.State.CONNECTED) getString(R.string.disconnect)
                                else getString(R.string.connect)
                        }
                    }, eventsHandler = { events ->
                        events.forEach {
                            speedometer.update(it)
                        }
                    })
                    speedometer.cleanTime()
                }
            }
        speedometer.start()
    }

}