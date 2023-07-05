package com.dxfeed.latencytestapp.tools

import com.devexperts.logging.Logging
import com.dxfeed.event.market.MarketEvent
import com.dxfeed.event.market.Quote
import com.dxfeed.event.market.TimeAndSale
import com.dxfeed.event.market.Trade
import com.dxfeed.event.market.TradeETH
import java.time.LocalTime

import java.util.Timer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.timer
import kotlin.math.pow
import kotlin.math.sqrt
import java.time.Duration

class Speedometer(private val period: Long, private val handler: (Metrics) -> Unit) {
    private var countingTimer : Timer? = Timer()

    private var symbols = ConcurrentHashMap<String, String>()
    private var deltas = ConcurrentLinkedQueue<Double>()
    private val logger = Logging.getLogging(Speedometer::class.java)
    private var startDate: LocalTime? = null

    fun start() {

        countingTimer = timer("CountingTimer", false, 0L, period, action = {
            if (startDate == null) {
                startDate = LocalTime.now()
            }
            val dur = Duration.between(startDate, LocalTime.now())
            val currentDeltas = deltas.toList()
            val currentSymbols = symbols.toList()
            val min = currentDeltas.minByOrNull { it } ?: 0.0
            val max = currentDeltas.maxByOrNull { it } ?: 0.0
            val mean = if (currentDeltas.average().isNaN()) 0.0 else currentDeltas.average()
            val percentile = calculatePercentile(currentDeltas, 0.99)
            val stdDev = calculateStdDev(currentDeltas)
            var stdErr = calculateStdErr(currentDeltas, stdDev)
            if (stdErr.isNaN()) {
                stdErr = 0.0
            }
            deltas.clear()
            symbols.clear()
            val metrics = Metrics(
                rateOfEvent = currentDeltas.size.toDouble() / (period / 1000),
                min = min,
                max = max,
                mean = mean,
                percentile = percentile,
                sampleSize = currentDeltas.size,
                stdDev = stdDev,
                error = stdErr,
                rateOfSymbols = currentSymbols.size,
                measureInterval = period / 1000,
                currentTime = dur.toMillis())
            handler(metrics)
        })
    }
    fun cleanTime() {
        startDate = null
    }
    fun update(event: MarketEvent) {
        val currentTimestamp = System.currentTimeMillis().toDouble()

        when (event) {
            is Quote -> {
                symbols[event.eventSymbol] = event.eventSymbol
                deltas.add(currentTimestamp - event.time)
            }
            is TimeAndSale -> {
                symbols[event.eventSymbol] = event.eventSymbol
                deltas.add(currentTimestamp - event.time)
            }
            is TradeETH -> {
                symbols[event.eventSymbol] = event.eventSymbol
                deltas.add(currentTimestamp - event.time)
            }
            is Trade -> {
                symbols[event.eventSymbol] = event.eventSymbol
                deltas.add(currentTimestamp - event.time)
            }
        }
    }

    companion object {
        private fun calculatePercentile(values: List<Double>, excelPercentile: Double): Double {
            if (values.isEmpty()) {
                return 0.0
            }
            val sortedValues = values.sorted()
            val N = sortedValues.size
            var n = ((N - 1) * excelPercentile) + 1
            if (n == 1.0) {
                return sortedValues.first()
            }
            if (n == N.toDouble()) {
                return  sortedValues.last()
            }
            val k = n.toInt()
            var d = n - k
            return sortedValues[k-1] + (d * (sortedValues[k] - sortedValues[k-1]))
        }

        private fun calculateStdDev(values: List<Double>): Double {
            var stdDev = 0.0
            var count = values.size
            if (count <= 1) {
                return  stdDev
            }
            count -= 1
            val avg = values.average()
            val sum = values.sumOf {
                (it - avg).pow(2)
            }
            stdDev = sqrt(sum / count)
            return stdDev
        }

        private fun calculateStdErr(values: List<Double>, stdDev: Double): Double {
            val count = values.size.toDouble()
            return stdDev / sqrt(count)
        }
    }


}