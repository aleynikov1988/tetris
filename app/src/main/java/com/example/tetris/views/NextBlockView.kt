package com.example.tetris.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.tetris.models.AppModel

class NextBlockView : View {
    private var paint = Paint()
    private var model: AppModel? = null
    private var cellSize: Dimension = Dimension(0, 0)
    private var frameOffset: Dimension = Dimension(0, 0)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private data class Dimension(val width: Int, val height: Int)

    fun setModel(model: AppModel) {
        this.model = model
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val cellW = w / 4
        val cellH = h / 4
        val n = Math.min(cellW, cellH)

        cellSize = Dimension(n, n)

        val offsetX = (w - 4 * n) / 2
        val offsetY = (h - 4 * n) / 2
        frameOffset = Dimension(offsetX, offsetY)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)
    }

    private fun drawFrame(canvas: Canvas) {
        paint.color = Color.LTGRAY
        canvas.drawRect(0.0F, 0.0F, (cellSize.width * 4).toFloat(), (cellSize.height * 4).toFloat(), paint)
    }
}
