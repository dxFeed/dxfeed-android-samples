package com.dxfeed.perftestapp.tools

data class Metrics(
    val rateOfEvent: Double,
    val rateOfListeners: Double,
    val numberOfEventsInCall: Double,
    val currentMemoryUsage: Long,
    val peakMemoryUsage: Long,
    val currentCpuUsage: Double,
    val peakCpuUsage: Double,
    val currentTime: Long
)
