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

class MarketDepthAdapter(symbol: String,
                         address: String,
                         isWebSocket: Boolean) : RecyclerView.Adapter<MarketDepthAdapter.ViewHolder>() {
    var orderBook: MarketDepthModel<Order>? = null
    val endpoint = DXEndpoint.create(DXEndpoint.Role.FEED)

    private var size: Float = 100f
    private var dataSource = mutableListOf<OrderModel>()

    private var maxSize = 0.0

    init {
        if (isWebSocket) {
            // The experimental property must be enabled.
            System.setProperty("dxfeed.experimental.dxlink.enable", "true")
            System.setProperty("scheme", "ext:opt:sysprops,resource:dxlink.xml")
        }
        endpoint?.connect(address)
        this.orderBook = MarketDepthModel.newBuilder(Order::class.java)
            .withListener { book ->
                dataSource.clear()
                var maxValue = 0.0
                book.sellOrders.forEach {
                    dataSource.add(OrderModel(it, false))
                    maxValue = max(maxValue, it.sizeAsDouble)
                }
                book.buyOrders.forEach {
                    dataSource.add(OrderModel(it, true))
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
            .withDepthLimit(3)
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
        orderBook?.depthLimit = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dom_order_item, parent, false)
        return ViewHolder(view).apply {
            view.layoutParams.height = size.toInt()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = dataSource[position]
        holder.bindOrder(model, maxSize)
    }

    override fun getItemCount(): Int {
        return dataSource.count()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val buyBackground = itemView.findViewById<PartialBackgroundView>(R.id.buy_partial_background_view).apply {
            setColor(ColorUtils.setAlphaComponent( ContextCompat.getColor(context, R.color.green), 0x4c))
        }
        private val sellBackground = itemView.findViewById<PartialBackgroundView>(R.id.sell_partial_background_view).apply {
            setColor(ColorUtils.setAlphaComponent( ContextCompat.getColor(context, R.color.red), 0x4c))
        }
        private val buySize = itemView.findViewById<TextView>(R.id.buySize)
        private val price = itemView.findViewById<TextView>(R.id.price)
        private val sellSize = itemView.findViewById<TextView>(R.id.sellSize)

        fun bindOrder(model: OrderModel, maxSize: Double) {
            val size = model.sizeString
            val isBuy = model.isBuy
            buySize.text = size
            sellSize.text = size
            price.text = model.priceString

            buySize.isVisible = isBuy
            sellSize.isVisible = !isBuy

            buyBackground.setFillPercentage(
                (model.size/ maxSize).toFloat(),
                !isBuy
            )
            buyBackground.isVisible = isBuy
            sellBackground.setFillPercentage(
                (model.size/ maxSize).toFloat(),
                !isBuy
            )
            sellBackground.isVisible = !isBuy
        }
    }
}