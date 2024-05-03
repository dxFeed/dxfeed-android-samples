package com.dxfeed.quotetableapp.adapters

import com.dxfeed.event.market.Profile
import com.dxfeed.event.market.Quote
import java.text.DecimalFormat

class QuoteModel(val symbol: String) {
    private var quote: Quote? = null
    private var previousQuote: Quote? = null
    private var profileDesc: String = ""
    private val df = DecimalFormat().also {
        it.maximumFractionDigits = 4
        it.isGroupingUsed = false
    }

    fun update(quote: Quote) {
        previousQuote = this.quote
        this.quote = quote
    }
    private fun format(price: Double): String {
        return df.format(price)
    }
    fun update(profile: Profile) {
        profileDesc = profile.description
    }

    val ask: String
        get() = format(quote?.askPrice ?: 0.0)

    val bid: String
        get() = format(quote?.bidPrice ?: 0.0)

    val description: String
        get() = profileDesc

    val increasedBid: Boolean?
        get() {
            if (previousQuote == null || quote == null) {
                return null
            }
            return quote!!.bidPrice > previousQuote!!.bidPrice
        }

    val increaseAsk: Boolean?
        get() {
            if (previousQuote == null || quote == null) {
                return null
            }
            return quote!!.askPrice > previousQuote!!.askPrice
        }
}