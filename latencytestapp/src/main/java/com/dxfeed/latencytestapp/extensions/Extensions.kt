package com.dxfeed.latencytestapp.extensions
import android.content.Context
import com.dxfeed.api.DXEndpoint
import com.dxfeed.latencytestapp.R
import java.text.DecimalFormat

fun Long.toTimeFormat(): String {
    val inSec = this / 1000
    val millis = this % 1000
    val seconds = inSec  % 60
    val minutes = (inSec / 60) % 60
    val hours = inSec / 3600
    return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}.${"%03d".format(millis)}"
}
fun Double.format(fractionDigits: Int): String {
    val df = DecimalFormat()
    df.maximumFractionDigits = fractionDigits
    return df.format(this)
}

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