package com.dxfeed.latencytestapp.tools

import com.devexperts.logging.Logging
import com.dxfeed.api.DXEndpoint
import com.dxfeed.event.market.MarketEvent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QDService() {
    val state: DXEndpoint.State
        get() = endpoint?.state ?: DXEndpoint.State.NOT_CONNECTED
    private var endpoint: DXEndpoint? = null
    private val executorService: ExecutorService = Executors.newFixedThreadPool(1)

    fun connect(address: String,
                symbols: List<String>,
                eventTypes: List<Class<out MarketEvent>>,
                connectionHandler: (DXEndpoint.State) -> Unit,
                eventsHandler: (List<MarketEvent>) -> Unit){
        System.setProperty("com.devexperts.connector.proto.heartbeatTimeout", "10s")

        executorService.execute {
            endpoint = DXEndpoint.newBuilder().build()
            endpoint?.addStateChangeListener {
                connectionHandler(it.newValue as DXEndpoint.State)
            }
            endpoint?.connect(address)

            eventTypes.forEach { eventType ->
                val subscription = endpoint?.feed?.createSubscription(eventType)
                subscription?.addEventListener {
                    eventsHandler(it)
                }
                subscription?.addSymbols(symbols)
            }
        }
    }
    fun disconnect() {
        executorService.execute {
            endpoint?.disconnect()
        }
    }
}