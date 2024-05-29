package com.dxfeed.quotetableapp

import android.content.Context
import android.view.View
import android.widget.TextView
import com.devexperts.util.TimeFormat
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF


class CustomMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val tvContent: TextView

    init {
        // this markerview only displays a textview
        tvContent = findViewById(R.id.openContent)
        println(tvContent)
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        tvContent.text = ""
        (e as? CandleEntry)?.let {
            val close = it.close
            val open = it.open
            val high = it.high
            val low = it.low

            tvContent.text =
                    "Open: ${open} \t Close ${close}\n" +
                    "High: ${high} \t Low ${low}"
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), -height.toFloat())
    }
}