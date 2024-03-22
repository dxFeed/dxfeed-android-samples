package com.dxfeed.quotetableapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.event.market.Profile
import com.dxfeed.event.market.Quote
import com.dxfeed.quotetableapp.R

class QuoteAdapter(mList: List<String>) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {
    private val dataSource = LinkedHashMap(mList.associateWith {
        QuoteModel(it)
    })

    fun update(quote: Quote) {
        dataSource[quote.eventSymbol]?.update(quote)
    }

    fun update(profile: Profile) {
        dataSource[profile.eventSymbol]?.update(profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symbol = dataSource.keys.elementAt(position)
        dataSource[symbol]?.apply {
            holder.bind(this)
        }
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        private val greenColor: Int by lazy { ItemView.context.resources.getColor(R.color.green, null) }
        private val redColor: Int by lazy { ItemView.context.resources.getColor(R.color.red, null) }
        private val defaultPriceColor: Int by lazy { ItemView.context.resources.getColor(R.color.priceBackground, null) }

        private val textView: TextView = itemView.findViewById(R.id.symbol_text_view)
        private val askButton: Button = itemView.findViewById(R.id.ask_button)
        private val bidButton: Button = itemView.findViewById(R.id.bid_button)

        fun bind(quote: QuoteModel) {
            textView.text = quote.symbol + "\n" + quote?.description
            askButton.text = quote?.ask
            bidButton.text = quote?.bid
            askButton.setBackgroundColor(priceColor(quote?.increaseAsk))
            bidButton.setBackgroundColor(priceColor(quote?.increasedBid))
        }

        private fun priceColor(increased: Boolean?): Int {
            increased?.let {
                if (it) {
                    return greenColor
                } else {
                    return  redColor
                }
            } ?: return defaultPriceColor
        }
    }


}
