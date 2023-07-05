package com.dxfeed.latencytestapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devexperts.logging.Logging
import com.dxfeed.latencytestapp.tools.Metrics
import com.dxfeed.latencytestapp.R
import com.dxfeed.latencytestapp.extensions.*


class QuoteAdapter(private val mList: List<String>) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {
    private val logger = Logging.getLogging(QuoteAdapter::class.java)
    private val rateKey = "Rate of unique symbols per interval"
    private val min = "Min, ms"
    private val max = "Max, ms"
    private val percentile = "99th percentile, ms"
    private val mean = "Mean, ms"
    private val stdDev = "StdDev, ms"
    private val error = "Error, ms"
    private val sampleSize = "Sample size (N), events"
    private val measurementInterval = "Measurement interval, s"
    private val runningTime = "Running time"
    private val rateEvents = "Rate of events (avg), events/s"

    private val dataSource = linkedMapOf<String, String>(
        rateEvents to "",
        rateKey to "",
        min to "",
        max to "",
        percentile to "",
        mean to "",
        stdDev to "",
        error to "",
        sampleSize to "",
        measurementInterval to "",
        runningTime to "")
    fun reload(metrics: Metrics) {
        dataSource[rateEvents] = metrics.rateOfEvent.format(2)
        dataSource[rateKey] = metrics.rateOfSymbols.toString()
        dataSource[min] = metrics.min.format(2)
        dataSource[max] = metrics.max.format(2)
        dataSource[percentile] = metrics.percentile.format(2)
        dataSource[mean] = metrics.mean.format(2)
        dataSource[stdDev] = metrics.stdDev.format(2)
        dataSource[error] = metrics.error.format(2)
        dataSource[sampleSize] = metrics.sampleSize.toString()
        dataSource[measurementInterval] = metrics.measureInterval.toString()
        dataSource[runningTime] = metrics.currentTime.toTimeFormat()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = dataSource.keys.elementAt(position)
        val value = dataSource[key]
        holder.metricTextView.text = key
        holder.valueTextView.text = value
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val metricTextView: TextView = itemView.findViewById(R.id.metricTextView)
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
    }


}
