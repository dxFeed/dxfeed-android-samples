package com.dxfeed.quotetableapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class PartialBackgroundView : View {
    private var paint: Paint? = null
    private var fillPercentage = 0f
    private var startFromEnd = false

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint!!.color = 0
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        if (startFromEnd) {
            val fillWidth = (width * fillPercentage).toInt()
            canvas.drawRect((width - fillWidth).toFloat(), 0f, width.toFloat(), height.toFloat(), paint!!)
        } else {
            val fillWidth = (width * fillPercentage).toInt()
            canvas.drawRect(0f, 0f, fillWidth.toFloat(), height.toFloat(), paint!!)
        }
    }

    fun setColor(color: Int) {
        paint?.color = color
    }

    fun setFillPercentage(percentage: Float, startFromEnd: Boolean) {
        fillPercentage = percentage
        this.startFromEnd = startFromEnd
        invalidate()
    }
}