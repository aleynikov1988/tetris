package com.example.tetris.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.example.tetris.constants.CellConstants
import com.example.tetris.helpers.array2dOfByte
import com.example.tetris.models.Block

class NextBlockView : View {
    private var paint = Paint()
    private var block: Block? = null
    private var cellSize: Dimension = Dimension(0, 0)
    private var frameOffset: Dimension = Dimension(0, 0)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    companion object {
        private const val BLOCK_OFFSET = 2
        private const val FRAME_OFFSET_BASE = 2
        private const val ROW_COUNT = 4
        private const val COLUMN_COUNT = 4
    }

    private data class Dimension(val width: Int, val height: Int)

    fun setBlock(block: Block?) {
        this.block = block
        invalidate() // ->onDraw()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val cellW = (((w - 2 * FRAME_OFFSET_BASE) / COLUMN_COUNT) / 1.4).toInt()
        val cellH = (((h - 2 * FRAME_OFFSET_BASE) / ROW_COUNT) / 1.4).toInt()
        val n = Math.min(cellW, cellH)

        cellSize = Dimension(n, n)

        val offsetX = (w - COLUMN_COUNT * n)
        val offsetY = 0

        frameOffset = Dimension(offsetX, offsetY)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)

        if (block != null) {
            val field: Array<ByteArray> = array2dOfByte(ROW_COUNT, COLUMN_COUNT)
            val shape: Array<ByteArray>? = block?.getShape(block?.frameNumber as Int)

            if (shape != null) {
                for (i in shape.indices) {
                    for (j in 0 until shape[i].size) {
                        field[j][i] = shape[i][j]
                    }
                }
            }

            for (i in field.indices) {
                for (j in 0 until field[i].size) {
                    if (field[i][j] == CellConstants.EPHEMERAL.value) {
                        drawCell(canvas, i, j)
                    }
                }
            }
        }
    }

    private fun drawFrame(canvas: Canvas) {
        val offsetW = frameOffset.width.toFloat()
        val offsetH = frameOffset.height.toFloat()

        paint.color = Color.LTGRAY
        canvas.drawRect(offsetW, offsetH, width.toFloat(), width - offsetW, paint)


    }

    private fun drawCell(canvas: Canvas, x: Int, y: Int) {
        val t = (frameOffset.height + y * cellSize.height + BLOCK_OFFSET).toFloat()
        val l = (frameOffset.width + x * cellSize.width + BLOCK_OFFSET).toFloat()
        val b = (frameOffset.height + (y + 1) * cellSize.height + BLOCK_OFFSET).toFloat()
        val r = (frameOffset.width + (x + 1) * cellSize.width + BLOCK_OFFSET).toFloat()
        val rectf = RectF(l, t, r, b)

        paint.color = block?.colorRGB as Int
        canvas.drawRoundRect(rectf, 5F, 5F, paint)

        val rectf2 = RectF(l + 5, t + 5, r - 5, b - 5)
        paint.color = Color.GRAY

        canvas.drawRoundRect(rectf2, 5F, 5F, paint)
    }
}
