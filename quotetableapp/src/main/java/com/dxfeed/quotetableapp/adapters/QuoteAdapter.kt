package com.dxfeed.quotetableapp.adapters

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dxfeed.event.market.Profile
import com.dxfeed.event.market.Quote
import com.dxfeed.quotetableapp.R

class QuoteAdapter(mList: List<String>, private val action: (ActionType, String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class ActionType {
        Candle, DepthOfMarket
    }
    enum class ViewType {
        CELL,FOOTER
    }


    inline fun <reified T : Enum<T>> Int.toEnum(): T? {
        return enumValues<T>().firstOrNull { it.ordinal == this }
    }

    //Enum to Int
    inline fun <reified T : Enum<T>> T.toInt(): Int {
        return this.ordinal
    }

    private val dataSource = LinkedHashMap(mList.associateWith {
        QuoteModel(it)
    })

    fun update(quote: Quote): Int{
        dataSource[quote.eventSymbol]?.update(quote)
        return dataSource.keys.indexOf(quote.eventSymbol)
    }

    fun update(profile: Profile): Int {
        dataSource[profile.eventSymbol]?.update(profile)
        return dataSource.keys.indexOf(profile.eventSymbol)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vType = viewType.toEnum<ViewType>()
        if (vType == ViewType.FOOTER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.footer_view, parent, false)
            return FooterViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view_design, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {

            val symbol = dataSource.keys.elementAt(position)
            dataSource[symbol]?.apply {
                holder.bind(this, action)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position == dataSource.size) ViewType.FOOTER.toInt() else ViewType.CELL.toInt()


override fun getItemCount(): Int {
        return dataSource.size + 1
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        private val greenColor: Int by lazy { ItemView.context.resources.getColor(R.color.green, null) }
        private val redColor: Int by lazy { ItemView.context.resources.getColor(R.color.red, null) }
        private val defaultPriceColor: Int by lazy { ItemView.context.resources.getColor(R.color.priceBackground, null) }

        private val textView: TextView = itemView.findViewById(R.id.symbol_text_view)
        private val askButton: Button = itemView.findViewById(R.id.ask_button)
        private val bidButton: Button = itemView.findViewById(R.id.bid_button)

        fun bind(quote: QuoteModel, action: (ActionType, String) -> Unit) {
            textView.setOnClickListener {
                showPopupMenu(this.textView, quote, action)
            }
            bidButton.setOnClickListener {
                showPopupMenu(this.bidButton, quote, action)
            }
            askButton.setOnClickListener {
                showPopupMenu(this.askButton, quote, action)
            }
            textView.text = quote.symbol + "\n" + quote?.description
            askButton.text = quote?.ask
            bidButton.text = quote?.bid
            askButton.setBackgroundColor(priceColor(quote?.increaseAsk))
            bidButton.setBackgroundColor(priceColor(quote?.increasedBid))
        }
        private fun showPopupMenu(view: View, quote: QuoteModel, action: (ActionType, String) -> Unit) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_item1 -> {
                        action(ActionType.Candle, quote.symbol)
                        true
                    }
                    R.id.menu_item2 -> {
                        action(ActionType.DepthOfMarket, quote.symbol)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
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

    class FooterViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        private val textView: TextView = itemView.findViewById(R.id.symbol_text_view)

        fun bind(text: String) {
            textView.text = text
        }
    }

}
