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
import com.dxfeed.event.candle.Candle
import com.dxfeed.event.candle.CandleType
import com.dxfeed.quotetableapp.tools.CandlesService
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.SimpleDateFormat
import java.util.Date


class CandleChartActivity: AppCompatActivity() {
    lateinit var candleService: CandlesService
    lateinit var pointIcon: Drawable

    companion object {
        const val symbol = "symbol"
        const val address = "address"
        const val useWebSocket = "useWebSocket"
        const val maxCount = 150
    }
    var candles = listOf<Candle>()
    var resetScroll = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DXFeedSimpleAndroidApps)
        setContentView(R.layout.candle_chart_activity)

        val drawable = ContextCompat.getDrawable(this, android.R.drawable.radiobutton_off_background)


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

        candleService = CandlesService(intent.getStringExtra(address)!!, intent.getBooleanExtra(useWebSocket, false))
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
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
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
                candleService.connect(title!!, type) {
                    candles = it.takeLast(maxCount)
                    println(candles)
                    drawChart(candles)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        //set selected day by default
        spinner.setSelection(2)



    }

    private fun drawChart(candles: List<Candle>) {
        val candleStickChart = findViewById<CandleStickChart>(R.id.candle_stick_chart)
        if (candles.isNullOrEmpty()) {
            resetScroll = true
            candleStickChart.clear()
            candleStickChart.invalidate()
            return
        }
        val yValsCandleStick = candles.mapIndexed { index, candle ->
            if (candle.high == candle.low && candle.open == candle.close) {

                CandleEntry(
                    index.toFloat(),
                    candle.high.toFloat(),
                    candle.low.toFloat(),
                    candle.open.toFloat(),
                    candle.close.toFloat(),
                    pointIcon
                )
            } else {
                CandleEntry(
                    index.toFloat(),
                    candle.high.toFloat(),
                    candle.low.toFloat(),
                    candle.open.toFloat(),
                    candle.close.toFloat()
                )
            }
        }
        val set1 = CandleDataSet(yValsCandleStick, "DataSet 1")

//        set1.color = Color.rgb(80, 80, 80)
        set1.shadowColor = ContextCompat.getColor(this, R.color.priceBackground)
        set1.shadowWidth = 0.8f

        set1.decreasingColor = ContextCompat.getColor(this, R.color.red)
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = ContextCompat.getColor(this, R.color.green)
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.setDrawValues(false)

// create a data object with the datasets
        val data = CandleData(set1)
// set data
        candleStickChart.data = data
        candleStickChart.setVisibleXRangeMaximum(40f)
        if (resetScroll) {
            candleStickChart.moveViewToX((candles.count() - 1).toFloat())
            resetScroll = false
        }

        candleStickChart.invalidate()
    }

    private fun addCandleChart() {
        val candleStickChart = findViewById<CandleStickChart>(R.id.candle_stick_chart)
        candleStickChart.setNoDataText("")
        candleStickChart.description.text = ""
        candleStickChart.isHighlightPerDragEnabled = true
        candleStickChart.setPinchZoom(false)
        candleStickChart.isDoubleTapToZoomEnabled = false

        candleStickChart.setDrawBorders(true)
//        candleStickChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
//            override fun onValueSelected(e: Entry?, h: Highlight?) {
//                e?.let {
//
//                }
//            }
//
//            override fun onNothingSelected() {
//                println("onNothingSelected") }
//        })
//        candleStickChart.setBorderColor(resources.getColor(R.color.))

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
                val candle = candles[value.toInt()]
                val date = Date(candle.time)
                var formatter = SimpleDateFormat("dd/MM/yyyy")
                var string = formatter.format(date)
                return string
            }
        })
        val l = candleStickChart.legend
        l.isEnabled = false

        val mv = CustomMarkerView(this, R.layout.marker_view)
        candleStickChart.marker = mv
        candleStickChart.isHighlightPerTapEnabled = true
    }
}
