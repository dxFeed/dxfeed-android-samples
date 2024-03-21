package com.dxfeed.quotetableapp.tools

import com.dxfeed.api.DXEndpoint
import com.dxfeed.event.market.MarketEvent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QDQuoteService(private val address: String, private val isWebSocket: Boolean) {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(1)

    fun connect(symbols: List<String>,
                eventTypes: List<Class<out MarketEvent>>,
                connectionHandler: (DXEndpoint.State) -> Unit,
                eventsHandler: (List<MarketEvent>) -> Unit){
        if (isWebSocket) {
            // The experimental property must be enabled.
            System.setProperty("dxfeed.experimental.dxlink.enable", "true")
        }
        executorService.execute {
            val endpoint = DXEndpoint
                .newBuilder()
                .withProperty("dxfeed.aggregationPeriod", "1")
                .build()
            endpoint.addStateChangeListener {
                connectionHandler(it.newValue as DXEndpoint.State)
            }
            endpoint.connect(address)

            eventTypes.forEach { eventType ->
                val subscription = endpoint?.feed?.createSubscription(eventType)
                subscription?.addEventListener {
                    eventsHandler(it)
                }
                subscription?.addSymbols(symbols)
            }
        }
    }
}