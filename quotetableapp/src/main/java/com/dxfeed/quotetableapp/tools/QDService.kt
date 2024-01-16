package com.dxfeed.quotetableapp.tools

import com.devexperts.logging.Logging
import com.dxfeed.api.DXEndpoint
import com.dxfeed.event.market.MarketEvent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QDService() {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(1)

    fun connect(address: String,
                symbols: List<String>,
                eventTypes: List<Class<out MarketEvent>>,
                connectionHandler: (DXEndpoint.State) -> Unit,
                eventsHandler: (List<MarketEvent>) -> Unit){
        System.setProperty("dxfeed.experimental.dxlink.enable", "true")
//        System.setProperty("scheme", "ext:resource:dxlink.xml")

        System.setProperty("com.devexperts.connector.proto.heartbeatTimeout", "10s")

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