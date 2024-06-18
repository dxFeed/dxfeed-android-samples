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
        paint = Paint().apply {
            color = 0
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint?.let { paint ->
            val width = width
            val fillWidth = (width * fillPercentage).toInt()
            if (startFromEnd) {
                canvas.drawRect(
                    (width - fillWidth).toFloat(),
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    paint
                )
            } else {
                canvas.drawRect(0f, 0f, fillWidth.toFloat(), height.toFloat(), paint)
            }
        }
    }

    fun setColor(color: Int) {
        paint?.color = color
    }

    fun setFillPercentage(percentage: Float, startFromEnd: Boolean) {
        this.fillPercentage = percentage
        this.startFromEnd = startFromEnd
        invalidate()
    }
}