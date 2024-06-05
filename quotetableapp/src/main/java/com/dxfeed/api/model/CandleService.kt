package com.dxfeed.api.model

import com.devexperts.util.TimeUtil
import com.dxfeed.api.DXEndpoint
import com.dxfeed.event.candle.Candle
import com.dxfeed.event.candle.CandlePeriod
import com.dxfeed.event.candle.CandleSymbol
import com.dxfeed.event.candle.CandleType
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CandleService(address: String, isWebSocket: Boolean) {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(1)
    var endpoint: DXEndpoint? = null
    private var candlesModel: TimeSeriesTxModel<Candle>? = null

    private val builder = TimeSeriesTxModel.newBuilder(Candle::class.java)

    init {
        if (isWebSocket) {
            // The experimental property must be enabled.
            System.setProperty("dxfeed.experimental.dxlink.enable", "true")
            System.setProperty("scheme", "ext:opt:sysprops,resource:dxlink.xml")
        }
        val endpoint = DXEndpoint
            .newBuilder()
            .withProperty("dxfeed.aggregationPeriod", "1")
            .build()
        endpoint.connect(address)
        this.endpoint = endpoint
        builder.withFeed(endpoint.feed)
    }
    fun close() {
        candlesModel?.close()
    }
    fun connect(symbol: String,
                type: CandleType,
                eventsHandler: (List<Candle>, Boolean) -> Unit){
        val period = CandlePeriod.valueOf(1.0, type)
        val startDate = when (type) {
            CandleType.MINUTE -> {
                System.currentTimeMillis() - 30 * TimeUtil.DAY
            }
            CandleType.HOUR -> {
                System.currentTimeMillis() - 30 * TimeUtil.DAY
            }
            CandleType.DAY -> {
                System.currentTimeMillis() - 365 * TimeUtil.DAY
            }
            CandleType.WEEK -> {
                System.currentTimeMillis() - 5 * 365 * TimeUtil.DAY
            }
            CandleType.MONTH -> {
                System.currentTimeMillis() - 10 * 365 * TimeUtil.DAY
            }
            CandleType.YEAR -> {
                0
            }
            else -> {
                0
            }
        }
        executorService.execute {
            candlesModel?.close()
            val candleSymbol = CandleSymbol.valueOf(symbol, period)
            builder.withSymbol(candleSymbol).withFromTime(startDate)
            builder.withExecutor(executorService)

            builder.withListener {
                eventsHandler(it.events, it.isSnapshot)
            }
            candlesModel = builder.build()
        }
    }

}