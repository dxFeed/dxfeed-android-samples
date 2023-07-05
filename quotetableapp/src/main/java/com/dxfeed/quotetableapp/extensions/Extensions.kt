package com.dxfeed.quotetableapp.extensions

import android.content.Context
import com.dxfeed.api.DXEndpoint
import com.dxfeed.quotetableapp.R
import java.text.DecimalFormat

fun DXEndpoint.State.stringValue(context: Context): String {
    return when (this) {
        DXEndpoint.State.CONNECTING -> context.getString(R.string.state_connecting)
        DXEndpoint.State.CONNECTED-> context.getString(R.string.state_connected)
        DXEndpoint.State.CLOSED -> context.getString(R.string.state_closed)
        else -> {
            context.getString(R.string.state_other)
        }
    }
}

fun Double.format(fractionDigits: Int): String {
    val df = DecimalFormat()
    df.maximumFractionDigits = fractionDigits
    return df.format(this)
}
