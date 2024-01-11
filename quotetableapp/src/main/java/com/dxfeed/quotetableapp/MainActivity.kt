package com.dxfeed.quotetableapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.api.DXEndpoint
import com.dxfeed.event.market.Quote

fun main() {
    // The experimental property must be enabled.
    System.setProperty("dxfeed.experimental.dxlink.enable", "true")
    // Set scheme for dxLink.
    System.setProperty("scheme", "ext:resource:dxlink.xml")
    val endpoint = DXEndpoint.newBuilder().build()
    val subscription = endpoint.feed.createSubscription(Quote::class.java)
    subscription?.addEventListener {
        it.forEach { event -> println(event) }

    }
    subscription?.addSymbols("AAPL")
    endpoint.connect("dxlink:wss://demo.dxfeed.com/dxlink-ws")
    Thread.sleep(10000)
}
