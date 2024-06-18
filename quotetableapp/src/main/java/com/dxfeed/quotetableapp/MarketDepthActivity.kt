package com.dxfeed.quotetableapp

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.adapters.MarketDepthAdapter
import com.dxfeed.quotetableapp.extensions.DividerItemDecoration



class MarketDepthActivity : AppCompatActivity() {
    companion object {
        const val symbol = "symbol"
        const val address = "address"
        const val useWebSocket = "useWebSocket"
        private const val orderItemHeight = 40f
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var marketDepthAdapter: MarketDepthAdapter

    override fun onDestroy() {
        super.onDestroy()
        marketDepthAdapter.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DXFeedSimpleAndroidApps)
        setContentView(R.layout.market_depth_activity)

        val symbolStr = intent.getStringExtra(CandleChartActivity.symbol)
        val symbolTitle = findViewById<TextView>(R.id.title)
        symbolTitle.text = symbolStr

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        marketDepthAdapter = MarketDepthAdapter(symbolStr!!,
            intent.getStringExtra(address)!!,
            intent.getBooleanExtra(useWebSocket, false))
        recyclerView.adapter = marketDepthAdapter
        val dividerItemDecoration = DividerItemDecoration(this)
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            val otherViewHeight: Int = recyclerView.height
            val scale: Float = applicationContext.resources.displayMetrics.density
            val defaultOrderHeight = orderItemHeight * scale + 0.5
            val numberOfRows: Int = ((otherViewHeight / defaultOrderHeight) / 2).toInt()
            val orderHeight = otherViewHeight / numberOfRows / 2
            marketDepthAdapter.setCellSize(orderHeight.toFloat())
            marketDepthAdapter.setNumberOfItems(numberOfRows)
        }
    }
}