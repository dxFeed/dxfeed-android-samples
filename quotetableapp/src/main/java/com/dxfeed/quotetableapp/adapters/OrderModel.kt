package com.dxfeed.quotetableapp.adapters

import com.dxfeed.event.market.Order
import java.text.DecimalFormat

class OrderModel(order: Order, val isBuy: Boolean) {
    companion object {
        private val df = DecimalFormat().also {
            it.maximumFractionDigits = 4
            it.isGroupingUsed = false
        }
    }
    private val price: Double = order.price
    val size: Double = order.sizeAsDouble

    private fun format(price: Double): String {
        return df.format(price)
    }

    val priceString: String
        get() = format(price)

    val sizeString: String
        get() = format(size)


}