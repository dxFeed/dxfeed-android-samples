package com.dxfeed.quotetableapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.api.DXEndpoint
import com.dxfeed.api.DXFeed
import com.dxfeed.api.model.MarketDepthListener.OrderBook
import com.dxfeed.api.model.MarketDepthModel
import com.dxfeed.event.market.Order
import com.dxfeed.event.market.OrderSource
import com.dxfeed.quotetableapp.R
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class PriceAdapter(private val priceList: List<PriceItem>, symbol: String, address: String, isWebSocket: Boolean) : RecyclerView.Adapter<PriceAdapter.ViewHolder>() {
    var orderBook: MarketDepthModel<Order>? = null
    val executor = Executors.newFixedThreadPool(1)
    val endpoint = DXEndpoint.create()

    private var size: Float = 100f
    private var numberOfItems: Int = 0
    init {
        if (isWebSocket) {
            // The experimental property must be enabled.
            System.setProperty("dxfeed.experimental.dxlink.enable", "true")
            System.setProperty("scheme", "ext:opt:sysprops,resource:dxlink.xml")
        }
        endpoint.addStateChangeListener {
            println("Change state ${it.newValue}")
        }

        endpoint?.connect(address)

        this.orderBook = MarketDepthModel.newBuilder(Order::class.java)
            .withListener {
                println("UPDATE BOOKS")
            }
            .withFeed(endpoint.feed)
//            .withSources(listOf(OrderSource.AGGREGATE_ASK, OrderSource.AGGREGATE_BID))
            .withSymbol(symbol)
            .withAggregationPeriod(1000, TimeUnit.MILLISECONDS)
            .withDepthLimit(10)
            .withExecutor(executor)
            .build()
    }

    fun setCellSize(size: Float) {
        this.size = size
    }

    fun setNumberOfItems(items: Int) {
       this.numberOfItems = items
//        orderBook?.depthLimit = items
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_price, parent, false)
        return ViewHolder(view).apply {
            view.layoutParams.height = size.toInt()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val priceItem = priceList[position]
        holder.buyPrice.text = priceItem.buyPrice
        holder.price.text = priceItem.price
        holder.sellPrice.text = priceItem.sellPrice
    }

    override fun getItemCount(): Int {
        return priceList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buyPrice: TextView = itemView.findViewById<TextView?>(R.id.bidPrice).apply {
            this.setBackgroundColor(
                ColorUtils.setAlphaComponent( ContextCompat.getColor(context, R.color.green), 30)
            )
        }
        val price: TextView = itemView.findViewById(R.id.price)
        val sellPrice: TextView = itemView.findViewById<TextView?>(R.id.askPrice).apply {
            this.setBackgroundColor(
                ColorUtils.setAlphaComponent( ContextCompat.getColor(context, R.color.red), 30)
            )
        }


    }
}