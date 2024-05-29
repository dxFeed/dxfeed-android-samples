package com.dxfeed.quotetableapp.tools

import com.devexperts.util.TimeUtil
import com.dxfeed.api.DXEndpoint
import com.dxfeed.api.osub.TimeSeriesSubscriptionSymbol
import com.dxfeed.event.candle.Candle
import com.dxfeed.event.candle.CandlePeriod
import com.dxfeed.event.candle.CandleSymbol
import com.dxfeed.event.candle.CandleType
import com.dxfeed.event.market.MarketEvent
import com.dxfeed.event.market.Quote
import com.dxfeed.model.TimeSeriesEventModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CandlesService(address: String, isWebSocket: Boolean) {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(1)
    var endpoint: DXEndpoint? = null


    val candlesModel = TimeSeriesEventModel(Candle::class.java)

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
        candlesModel.attach(endpoint.feed)
    }

    fun connect(symbol: String,
                type: CandleType,
                eventsHandler: (List<Candle>) -> Unit){
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
            candlesModel.clear()
            candlesModel.eventsList.addListener {
                eventsHandler(candlesModel.eventsList)
            }
            val candleSymbol = CandleSymbol.valueOf(symbol, period)
            candlesModel.symbol = candleSymbol
            candlesModel.fromTime = startDate
        }

    }



}