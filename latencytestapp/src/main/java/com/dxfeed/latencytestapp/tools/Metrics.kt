package com.dxfeed.latencytestapp.tools

data class Metrics(
    val rateOfEvent: Double,
    val min: Double,
    val max: Double,
    val mean: Double,
    val percentile: Double,
    val sampleSize: Int,
    val measureInterval: Long,
    val stdDev: Double,
    val error: Double,
    val rateOfSymbols: Int,
    val currentTime: Long
)
