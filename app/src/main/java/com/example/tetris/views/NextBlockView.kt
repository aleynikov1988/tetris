package com.example.tetris.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.tetris.models.Block
import com.example.tetris.models.Shape

class NextBlockView : View {
    private var paint = Paint()
    private var block: Block? = null
    private var cellSize: Dimension = Dimension(0, 0)
    private var frameOffset: Dimension = Dimension(0, 0)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private data class Dimension(val width: Int, val height: Int)

    fun setBlock(block: Block) {
        this.block = block
        invalidate() // ->onDraw()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val cellW = (w - 2 * 15) / 4
        val cellH = (h - 2 * 15) / 4
        val n = Math.min(cellW, cellH)

        cellSize = Dimension(n, n)

        val offsetX = (w - 4 * n) / 2
        val offsetY = (h - 4 * n) / 2
        frameOffset = Dimension(offsetX, offsetY)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)

        if (block != null) {
            val shape: Array<ByteArray>? = block?.getShape(block?.frameNumber as Int)

            if (shape != null) {
                for (row in shape.indices) {
                    for (col in 0 until shape[row].size) {
                        Log.d("NextBlockView", "DRAW CELL row:${row} col:${col}")
                        drawCell(canvas, row, col)
                    }
                }
            }
        }

    }

    private fun drawFrame(canvas: Canvas) {
        paint.color = Color.LTGRAY
        canvas.drawRect(.0F, .0F, (cellSize.width * 4).toFloat(), (cellSize.height * 4).toFloat(), paint)
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val left = (frameOffset.height + col * cellSize.height).toFloat()
        val top = (frameOffset.width + row * cellSize.width).toFloat()
        val right = (frameOffset.height + (col + 1) * cellSize.height).toFloat()
        val bottom = (frameOffset.width + (row + 1) * cellSize.width).toFloat()
        val rectf = RectF(left, top, right, bottom)

        paint.color = Color.BLACK
        canvas.drawRoundRect(rectf, 5F, 5F, paint)
    }
}
