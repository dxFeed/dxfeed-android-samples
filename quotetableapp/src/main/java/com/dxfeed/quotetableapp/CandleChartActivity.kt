package com.dxfeed.quotetableapp

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dxfeed.api.model.CandleService
import com.dxfeed.event.IndexedEvent
import com.dxfeed.event.candle.Candle
import com.dxfeed.event.candle.CandleType
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.lang.Float.max
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.Date

interface CandlesData {    // Not sure if this is correct
    fun getDate(xValue: Float?): Date
}

class CandleChartActivity : AppCompatActivity(), CandlesData {
    lateinit var pointIcon: Drawable
    lateinit var candleService: CandleService
    val entries = mutableMapOf<Long, Entry>()
    lateinit var candleStickChart: CandleStickChart
    val dateFormatter = SimpleDateFormat("MM.yyyy")
    val dateTimeFormatter = SimpleDateFormat("dd.MM.yy hh:mm")
    lateinit var candleType: CandleType
    companion object {
        const val symbol = "symbol"
        const val address = "address"
        const val useWebSocket = "useWebSocket"
        const val maxCount = 150
    }

    var localCandles = mutableListOf<Candle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DXFeedSimpleAndroidApps)
        setContentView(R.layout.candle_chart_activity)

        candleStickChart = findViewById(R.id.candle_stick_chart)
        val drawable =
            ContextCompat.getDrawable(this, android.R.drawable.radiobutton_off_background)


        val bitmap = (drawable as BitmapDrawable).bitmap
        val d: Drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 10, 10, true))
        val wrappedDrawable = DrawableCompat.wrap(d!!)
        DrawableCompat.setTint(
            wrappedDrawable,
            ContextCompat.getColor(this, R.color.green)
        )
        pointIcon = d

        val title = intent.getStringExtra(symbol)
        val symbolTitle = findViewById<TextView>(R.id.symbol_title)
        symbolTitle.text = title

        candleService = CandleService(
            intent.getStringExtra(address)!!,
            intent.getBooleanExtra(useWebSocket, false)
        )
        val spinner: Spinner = findViewById(R.id.spinnerOptions)

        addCandleChart()

        ArrayAdapter.createFromResource(
            this,
            R.array.candle_options,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val type = when (position) {
                    0 -> CandleType.MINUTE
                    1 -> CandleType.HOUR
                    2 -> CandleType.DAY
                    3 -> CandleType.WEEK
                    4 -> CandleType.MONTH
                    5 -> CandleType.YEAR
                    else -> {
                        CandleType.WEEK
                    }
                }
                candleType = type
                candleStickChart.highlightValue(null)
                candleStickChart.clear()
                candleService.connect(title!!, type) { list, isSnapshot ->
                    val list = list.reversed()
                    if (!localCandles.isNullOrEmpty()) {
                        println(localCandles.first())
                        println(localCandles.last())
                    }
                    if (isSnapshot) {
                        localCandles = list.takeLast(maxCount).toMutableList()
                        drawChart(localCandles)
                    } else {
                        updateChart(list)
                    }
                    println("Candles count ${localCandles.count()}")

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        //set selected day by default
        spinner.setSelection(0)
    }

    private fun updateChart(candles: List<Candle>) {
        candles.forEach {
            if ((it.eventFlags and IndexedEvent.REMOVE_EVENT) != 0) {
                // remove
                localCandles.removeIf { toRemove ->
                    toRemove.index == it.index
                }
                entries.remove(it.index)
            } else {
                // update
                val entry = entries.get(it.index)
                if (entry != null) {
                    val newEntry = convertToCandleEntry(it, entry.x)
                    localCandles.replaceAll { toReplace ->
                        if (toReplace.index == it.index) {
                            it
                        } else {
                            toReplace
                        }
                    }
                    entries[it.index] = newEntry
                    candleStickChart.data.removeEntry(entry, 0)
                    candleStickChart.data.addEntry(newEntry, 0)
                    candleStickChart.data.notifyDataChanged()
                    candleStickChart.notifyDataSetChanged(); // let the chart know it's data changed
                    candleStickChart.invalidate();
                } else {
                    // insert
                    val newEntry = convertToCandleEntry(it, entries.count().toFloat())
                    localCandles.add(it)
                    entries[it.index] = newEntry
                    candleStickChart.data.addEntry(newEntry, 0)
                    candleStickChart.data.notifyDataChanged()
                    candleStickChart.notifyDataSetChanged(); // let the chart know it's data changed
                    candleStickChart.invalidate();
                }
            }
        }
    }
    private fun convertToCandleEntry(candle: Candle, index: Float): CandleEntry {
        val entry = if (candle.high == candle.low && candle.open == candle.close) {

            CandleEntry(
                index,
                candle.high.toFloat(),
                candle.low.toFloat(),
                candle.open.toFloat(),
                candle.close.toFloat(),
                pointIcon
            )
        } else {
            CandleEntry(
                index,
                candle.high.toFloat(),
                candle.low.toFloat(),
                candle.open.toFloat(),
                candle.close.toFloat()
            )
        }
        return entry
    }
    private fun drawChart(candles: List<Candle>) {
        entries.clear()
        val yValsCandleStick = candles.mapIndexed { index, candle ->
            val entry = convertToCandleEntry(candle, index.toFloat())
            entries.put(candle.index, entry)
            entry
        }
        val set1 = CandleDataSet(yValsCandleStick, "DataSet 1")

        set1.shadowColor = ContextCompat.getColor(this, R.color.priceBackground)
        set1.shadowWidth = 0.8f

        set1.decreasingColor = ContextCompat.getColor(this, R.color.red)
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = ContextCompat.getColor(this, R.color.green)
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.setDrawValues(false)

        val data = CandleData(set1)
        candleStickChart.data = data
        data.notifyDataChanged()

        candleStickChart.notifyDataSetChanged()
        candleStickChart.setVisibleXRange(0f, min(localCandles.count(), 30).toFloat())
        candleStickChart.moveViewToX(data.entryCount.toFloat())

    }

    private fun addCandleChart() {
//        candleStickChart.onChartGestureListener = object : OnChartGestureListener {
//            override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
//                println("onChartGestureStart")
//            }
//
//            override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
//                println("onChartGestureEnd")
//            }
//
//            override fun onChartLongPressed(me: MotionEvent?) {
//                println("onChartLongPressed")
//            }
//
//            override fun onChartDoubleTapped(me: MotionEvent?) {
//                println("onChartDoubleTapped")
//            }
//
//            override fun onChartSingleTapped(me: MotionEvent?) {
//                println("onChartSingleTapped")
//            }
//
//            override fun onChartFling(
//                me1: MotionEvent?,
//                me2: MotionEvent?,
//                velocityX: Float,
//                velocityY: Float
//            ) {
//                println("onChartFling")
//            }
//
//            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
//                println("onChartScale")
//            }
//
//            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
//                println("onChartTranslate")
//            }
//        }

        candleStickChart.setNoDataText("")
        candleStickChart.description.text = ""
        candleStickChart.isHighlightPerDragEnabled = true
        candleStickChart.setPinchZoom(false)
        candleStickChart.isDoubleTapToZoomEnabled = false

        candleStickChart.setDrawBorders(true)
        val yAxis = candleStickChart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.setDrawLabels(false)

        val rightAxis = candleStickChart.axisRight
        rightAxis.textColor = ContextCompat.getColor(this, R.color.white)
        rightAxis.setDrawGridLines(false)
        candleStickChart.requestDisallowInterceptTouchEvent(true)

        val xAxis = candleStickChart.xAxis
        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(true)
        xAxis.isGranularityEnabled = false
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.textColor = ContextCompat.getColor(this, R.color.white)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 4
        xAxis.setValueFormatter(object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value >= localCandles.count().toFloat()) {
                    return ""
                }
                val candle = localCandles[value.toInt()]
                val date = Date(candle.time)
                if (candleType == CandleType.YEAR) {
                    return dateFormatter.format(date)
                }
                return dateTimeFormatter.format(date)
            }
        })
        val l = candleStickChart.legend
        l.isEnabled = false

        val mv = CustomMarkerView(this, R.layout.marker_view).apply {
            chartView = candleStickChart
        }
        candleStickChart.marker = mv
        candleStickChart.isHighlightPerTapEnabled = true
    }

    override fun getDate(xValue: Float?): Date {
        if (xValue != null) {
            if (xValue >= localCandles.count().toFloat()) {
                return Date()
            }
            val candle = localCandles[xValue.toInt()]
            return Date(candle.time)
        } else {
            return Date()
        }
    }
}
