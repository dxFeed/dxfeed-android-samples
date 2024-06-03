package com.dxfeed.quotetableapp

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat


class CustomMarkerView(val context1: Context?, layoutResource: Int) :
    MarkerView(context1, layoutResource) {
    private val openText: TextView = findViewById(R.id.openContent)
    private val closeText: TextView = findViewById(R.id.closeContent)
    private val highText: TextView = findViewById(R.id.highContent)
    private val lowText: TextView = findViewById(R.id.lowContent)
    private val dateText: TextView = findViewById(R.id.dateContent)
    private val dateFormater = SimpleDateFormat()
    init {
                // this markerview only displays a textview
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        (e as? CandleEntry)?.let {
            openText.text = "Open: ${it.open}"
            closeText.text = "Close: ${it.close}"
            highText.text = "High: ${it.high}"
            lowText.text = "Low: ${it.low}"
        }

        (context1 as? CandlesData)?.let {
            val date = it.getDate(e?.x)
            dateText.text =  dateFormater.format(date)
        }
    }
}