package com.example.tetris.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tetris.activities.GameActivity
import com.example.tetris.constants.CellConstants
import com.example.tetris.constants.FieldConstants
import com.example.tetris.models.Block
import com.example.tetris.models.Tetris

class TetrisView : View {
    private var paint = Paint()
    private var lastMove: Long = 0
    private var tetris: Tetris? = null
    private var game: GameActivity? = null
    private var viewHandler: ViewHandler = ViewHandler(this)
    private var cellSize: Dimension = Dimension(0, 0)
    private var frameOffset: Dimension = Dimension(0, 0)
    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.CYAN
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 36f
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    companion object {
        private val DELAY = 500
        private val BLOCK_OFFSET = 2
        private val FRAME_OFFSET_BASE = 0
    }

    fun setTetrisModel(model: Tetris) {
        tetris = model
    }

    fun setGameActivity(activity: GameActivity) {
        game = activity
    }

    fun setGameCommand(cmd: Tetris.Motions) {
        if (tetris != null && tetris?.currentState == Tetris.Statuses.ACTIVE.name) {
            tetris?.generateField(cmd.name)
            invalidate() // -> onDraw()
        }
    }

    fun setGameCommandWithDelay(cmd: Tetris.Motions) {
        val now = System.currentTimeMillis()

        if (now - lastMove > DELAY) {
            tetris?.generateField(cmd.name)
            invalidate() // -> onDraw()
            lastMove = now
        }
        updateScores()
        viewHandler.sleep(DELAY.toLong())
    }

    private class ViewHandler(private val owner: TetrisView) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                if (owner.tetris != null) {
                    if (owner.tetris!!.isGameActive()) {
                        owner.setGameCommandWithDelay(Tetris.Motions.DOWN)
                    }
                    if (owner.tetris!!.isGameOver()) {
                        owner.tetris?.endGame()
                        Toast.makeText(owner.game, "Game is over", Toast.LENGTH_LONG).show()
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

        val xPos = (canvas.width / 2).toFloat()
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

        if (tetris?.isGameAwaitingStart() as Boolean) {
            canvas.drawText("Tap to start".toUpperCase(), xPos, yPos, textPaint)
        }

        if (tetris?.isGameOver() as Boolean) {
            canvas.drawText("Game is over".toUpperCase(), xPos, yPos, textPaint)
        }

        if (tetris != null) {
            for (row in 0 until FieldConstants.ROW_COUNT.value) {
                for (col in 0 until FieldConstants.COLUMN_COUNT.value) {
                    drawCell(canvas, row, col)
                }
            }
        }
    }

    private fun drawFrame(canvas: Canvas) {
        val offsetW = frameOffset.width.toFloat()
        val offsetH = frameOffset.height.toFloat()

        paint.color = Color.BLACK
        canvas.drawRect(offsetW, offsetH, width - offsetW, height - offsetH, paint)
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val status = tetris?.getCellStatus(row, col)

        if (CellConstants.EMPTY.value != status) {
            val rgbValue = if (CellConstants.EPHEMERAL.value == status) {
                tetris?.currentBlock?.colorRGB
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

        val rectf2 = RectF(l + 5, t + 5, r - 5, b - 5)
        paint.color = Color.GRAY

        canvas.drawRoundRect(rectf2, 5F, 5F, paint)
    }

    private fun updateScores() {
        game?.updateCurrentScore(tetris?.score as Int)
        game?.updateHighScore()
    }
}
