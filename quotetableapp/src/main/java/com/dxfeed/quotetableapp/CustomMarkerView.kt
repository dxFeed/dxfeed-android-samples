package com.dxfeed.quotetableapp

import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.DecimalFormat
import java.text.SimpleDateFormat


class CustomMarkerView(val context1: Context?, layoutResource: Int) :
    MarkerView(context1, layoutResource) {
    companion object {
        private val df = DecimalFormat().also {
            it.maximumFractionDigits = 4
            it.isGroupingUsed = false
        }
    }
    private val openText: TextView = findViewById(R.id.openContent)
    private val closeText: TextView = findViewById(R.id.closeContent)
    private val highText: TextView = findViewById(R.id.highContent)
    private val lowText: TextView = findViewById(R.id.lowContent)
    private val dateText: TextView = findViewById(R.id.dateContent)
    private val dateFormatter = SimpleDateFormat()
    var drawingPosX: Float = 0f
    var drawingPosY: Float = 0f
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        (e as? CandleEntry)?.let {
            openText.text = "Open: ${df.format(it.open)}"
            closeText.text = "Close: ${df.format(it.close)}"
            highText.text = "High: ${df.format(it.high)}"
            lowText.text = "Low: ${df.format(it.low)}"
        }

        (context1 as? CandlesData)?.let {
            val date = it.getDate(e?.x)
            dateText.text =  dateFormatter.format(date)
        }
        super.refreshContent(e, highlight)

    }
    override fun draw(canvas: Canvas?, posX: Float, posY: Float) {
        super.draw(canvas, posX, posY)
        val offset = getOffsetForDrawingAtPoint(posX, posY)
        this.drawingPosX = posX + offset.x
        this.drawingPosY = posY + offset.y
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        return MPPointF(-posX + getResources().getDisplayMetrics().widthPixels/2 - width/2, -posY + 10)
    }

}