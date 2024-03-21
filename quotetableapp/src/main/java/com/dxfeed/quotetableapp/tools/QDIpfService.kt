package com.dxfeed.quotetableapp.tools

import com.dxfeed.ipf.InstrumentProfile
import com.dxfeed.ipf.InstrumentProfileReader
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QDIpfService(private val address: String) {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(1)

    fun connect(eventsHandler: (List<InstrumentProfile>) -> Unit){
        executorService.execute {
            try {
                val reader = InstrumentProfileReader()
                val results = reader.readFromFile(address)
                eventsHandler(results)
            } catch (ex: IOException) {
                println("QDIpfService error: $ex")
            }

        }
    }
}