package com.example.tetris.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.example.tetris.activities.GameActivity
import com.example.tetris.constants.CellConstants
import com.example.tetris.constants.FieldConstants
import com.example.tetris.models.AppModel
import com.example.tetris.models.Block

class TetrisView : View {
    private var paint = Paint()
    private var lastMove: Long = 0
    private var model: AppModel? = null
    private var activity: GameActivity? = null
    private var viewHandler: ViewHandler = ViewHandler(this)
    private var cellSize: Dimension = Dimension(0, 0)
    private var frameOffset: Dimension = Dimension(0, 0)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    companion object {
        private val DELAY = 500
        private val BLOCK_OFFSET = 2
        private val FRAME_OFFSET_BASE = 10
    }

    private class ViewHandler(private val owner: TetrisView) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                if (owner.model != null) {
                    if (owner.model!!.isGameActive()) {
                        owner.setGameCommandWithDelay(AppModel.Motions.DOWN)
                    }
                    if (owner.model!!.isGameOver()) {
                        owner.model?.endGame()
                        Toast.makeText(owner.activity, "Game is over", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        fun sleep(delay: Long) {
            removeMessages(0)
            sendMessageDelayed(obtainMessage(0), delay)
        }
    }

    private data class Dimension(val width: Int, val height: Int)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val cellW = (w - 2 * FRAME_OFFSET_BASE) / FieldConstants.COLUMN_COUNT.value
        val cellH = (h - 2 * FRAME_OFFSET_BASE) / FieldConstants.ROW_COUNT.value
        val n = Math.min(cellW, cellH)

        cellSize = Dimension(n, n)

        val offsetX = (w - FieldConstants.COLUMN_COUNT.value * n) / 2
        val offsetY = (h - FieldConstants.ROW_COUNT.value * n) / 2
        frameOffset = Dimension(offsetX, offsetY)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)

        if (model != null) {
            for (row in 0 until FieldConstants.ROW_COUNT.value) {
                for (col in 0 until FieldConstants.COLUMN_COUNT.value) {
                    drawCell(canvas, row, col)
                }
            }
        }
    }

    fun setModel(model: AppModel) {
        this.model = model
    }

    fun setActivity(activity: GameActivity) {
        this.activity = activity
    }

    fun setGameCommand(cmd: AppModel.Motions) {
        if (model != null && model?.currentState == AppModel.Statuses.ACTIVE.name) {
            if (cmd == AppModel.Motions.DOWN) {
                model?.generateField(cmd.name)
                invalidate() // -> onDraw()
                return
            }
            setGameCommandWithDelay(cmd)
        }
    }

    fun setGameCommandWithDelay(cmd: AppModel.Motions) {
        val now = System.currentTimeMillis()

        if (now - lastMove > DELAY) {
            model?.generateField(cmd.name)
            invalidate() // -> onDraw()
            lastMove = now
        }
        updateScores()
        viewHandler.sleep(DELAY.toLong())
    }

    private fun drawFrame(canvas: Canvas) {
        val offsetW = frameOffset.width.toFloat()
        val offsetH = frameOffset.height.toFloat()

        paint.color = Color.BLACK
        canvas.drawRect(offsetW, offsetH, width - offsetW, height - offsetH, paint)
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val status = model?.getCellStatus(row, col)

        if (CellConstants.EMPTY.value != status) {
            val rgbValue = if (CellConstants.EPHEMERAL.value == status) {
                model?.currentBlock?.colorRGB
            } else {
                Block.getColorRGB(status as Byte)
            }
            drawCell(canvas, col, row, rgbValue as Int)
        }
    }

    private fun drawCell(canvas: Canvas, x: Int, y: Int, rgbValue: Int) {
        val t = (frameOffset.height + y * cellSize.height + BLOCK_OFFSET).toFloat()
        val l = (frameOffset.width + x * cellSize.width + BLOCK_OFFSET).toFloat()
        val b = (frameOffset.height + (y + 1) * cellSize.height + BLOCK_OFFSET).toFloat()
        val r = (frameOffset.width + (x + 1) * cellSize.width + BLOCK_OFFSET).toFloat()
        val rectf = RectF(l, t, r, b)

        paint.color = rgbValue
        canvas.drawRoundRect(rectf, 5F, 5F, paint)
    }

    private fun updateScores() {
        activity?.updateCurrentScore(model?.score as Int)
        activity?.updateHighScore()
    }
}
