package com.dxfeed.quotetableapp

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.adapters.PriceAdapter
import com.dxfeed.quotetableapp.extensions.DividerItemDecoration



class MarketDepthActivity : AppCompatActivity() {
    companion object {
        const val symbol = "symbol"
        const val address = "address"
        const val useWebSocket = "useWebSocket"
        private const val orderItemHeight = 40f
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var priceAdapter: PriceAdapter

    override fun onDestroy() {
        super.onDestroy()
        priceAdapter.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DXFeedSimpleAndroidApps)
        setContentView(R.layout.market_depth_activity)

        val otherView = findViewById<View>(R.id.recyclerView)
        otherView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        otherView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val otherViewHeight: Int = otherView.getHeight()
                val scale: Float = applicationContext.getResources().getDisplayMetrics().density
                val defaultOrderHeight = orderItemHeight * scale + 0.5
                val numberOfRows: Int = ((otherViewHeight / defaultOrderHeight) / 2).toInt()
                val orderHeight = otherViewHeight / numberOfRows / 2
                priceAdapter.setCellSize(orderHeight.toFloat())
                priceAdapter.setNumberOfItems(numberOfRows)
            }
        })
        val symbolStr = intent.getStringExtra(CandleChartActivity.symbol)
        val symbolTitle = findViewById<TextView>(R.id.title)
        symbolTitle.text = symbolStr

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        priceAdapter = PriceAdapter(symbolStr!!,
            intent.getStringExtra(address)!!,
            intent.getBooleanExtra(useWebSocket, false))
        recyclerView.adapter = priceAdapter

        val dividerItemDecoration = DividerItemDecoration(this)
        recyclerView.addItemDecoration(dividerItemDecoration)

    }
}