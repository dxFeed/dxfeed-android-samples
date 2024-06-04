package com.dxfeed.quotetableapp

import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.quotetableapp.adapters.PriceAdapter
import com.dxfeed.quotetableapp.adapters.PriceItem


class MarketDepthActivity : AppCompatActivity() {
    companion object {
        const val symbol = "symbol"
        const val address = "address"
        const val useWebSocket = "useWebSocket"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var priceAdapter: PriceAdapter
    private lateinit var priceList: MutableList<PriceItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DXFeedSimpleAndroidApps)
        setContentView(R.layout.market_depth_activity)

        val otherView = findViewById<View>(R.id.recyclerView)
        otherView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        otherView.addOnLayoutChangeListener(object: OnLayoutChangeListener {
            override fun onLayoutChange(
                var1: View?,
                var2: Int,
                var3: Int,
                var4: Int,
                var5: Int,
                var6: Int,
                var7: Int,
                var8: Int,
                var9: Int
            ) {
                val otherViewHeight = otherView.height
                val defaultCellSize = 50f
                val numberOfRows = (otherViewHeight / defaultCellSize) / 2
                val cellSize = otherViewHeight / numberOfRows / 2
                priceAdapter.setCellSize(cellSize)
                priceAdapter.setNumberOfItems(numberOfRows.toInt())
            }
        })


        val title = intent.getStringExtra(CandleChartActivity.symbol)
        val symbolTitle = findViewById<TextView>(R.id.title)
        symbolTitle.text = title

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        priceList = mutableListOf()
        // Add sample data to the list
        priceList.add(PriceItem("0.1254", "3763.62", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))
        priceList.add(PriceItem("0.1254", "3763.62123123", "0.1594"))

        // ... add more items
        val scale: Float = this.getResources().getDisplayMetrics().density

        priceAdapter = PriceAdapter(priceList, symbol,
            intent.getStringExtra(MarketDepthActivity.address)!!,
            intent.getBooleanExtra(MarketDepthActivity.useWebSocket, false))
        recyclerView.adapter = priceAdapter


    }


}