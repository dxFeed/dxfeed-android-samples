package com.dxfeed.quotetableapp.adapters

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.api.DXEndpoint
import com.dxfeed.api.model.MarketDepthModel
import com.dxfeed.event.market.Order
import com.dxfeed.event.market.OrderSource
import com.dxfeed.quotetableapp.PartialBackgroundView
import com.dxfeed.quotetableapp.R
import java.lang.Double.max
import java.text.DecimalFormat
import java.util.concurrent.Executors

class PriceAdapter(symbol: String,
                   address: String,
                   isWebSocket: Boolean) : RecyclerView.Adapter<PriceAdapter.ViewHolder>() {
    var orderBook: MarketDepthModel<Order>? = null
    val endpoint = DXEndpoint.create(DXEndpoint.Role.FEED)

    private var size: Float = 100f
    private var numberOfItems: Int = 0

    private var buyOrders = listOf<Order>()
    private var sellOrders = listOf<Order>()
    private var maxSize = 0.0


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
                buyOrders = it.buyOrders
                sellOrders = it.sellOrders
                var maxValue = 0.0
                buyOrders.forEach {
                    maxValue = max(maxValue, it.sizeAsDouble)
                }
                sellOrders.forEach {
                    maxValue = max(maxValue, it.sizeAsDouble)
                }
                maxSize = maxValue
                Handler(Looper.getMainLooper()).post {
                    this.notifyDataSetChanged()
                }
            }
            .withFeed(endpoint.feed)
            .withSources(listOf(OrderSource.AGGREGATE_ASK, OrderSource.AGGREGATE_BID))
            .withSymbol(symbol)
            .withDepthLimit(2)
            .withExecutor(Executors.newSingleThreadScheduledExecutor())
            .build()
    }

    fun close() {
        orderBook?.close()
    }
    fun setCellSize(size: Float) {
        this.size = size
    }

    fun setNumberOfItems(items: Int) {
       this.numberOfItems = items
        orderBook?.depthLimit = items
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_price, parent, false)
        return ViewHolder(view).apply {
            view.layoutParams.height = size.toInt()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isBuy = position >= sellOrders.count()
        var order = if (position < sellOrders.count()) sellOrders[position] else buyOrders[position - sellOrders.count()]
        holder.bindOrder(order, isBuy, maxSize)
    }

    override fun getItemCount(): Int {
        return sellOrders.count() + buyOrders.count()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            private val df = DecimalFormat().also {
                it.maximumFractionDigits = 4
                it.isGroupingUsed = false
            }

        }
        private val buyBackground = itemView.findViewById<PartialBackgroundView>(R.id.buy_partial_background_view).apply {
            setColor(ColorUtils.setAlphaComponent( ContextCompat.getColor(context, R.color.green), 0x4c))
        }
        private val sellBackground = itemView.findViewById<PartialBackgroundView>(R.id.sell_partial_background_view).apply {
            setColor(ColorUtils.setAlphaComponent( ContextCompat.getColor(context, R.color.red), 0x4c))
        }
        private val buySize = itemView.findViewById<TextView>(R.id.buySize)
        private val price = itemView.findViewById<TextView>(R.id.price)
        private val sellSize = itemView.findViewById<TextView>(R.id.sellSize)

        fun bindOrder(order: Order, isBuy: Boolean, maxSize: Double) {
            val size = df.format(order.sizeAsDouble)
            buySize.text = size
            sellSize.text = size
            price.text = df.format(order.price)

            buySize.isVisible = isBuy
            sellSize.isVisible = !isBuy

            buyBackground.setFillPercentage(
                (order.sizeAsDouble/ maxSize).toFloat(),
                !isBuy
            )
            buyBackground.isVisible = isBuy
            sellBackground.setFillPercentage(
                (order.sizeAsDouble/ maxSize).toFloat(),
                !isBuy
            )
            sellBackground.isVisible = !isBuy
        }
    }
}