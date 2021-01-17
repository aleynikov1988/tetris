package com.example.tetris.models

import android.graphics.Point
import android.util.Log
import com.example.tetris.constants.CellConstants
import com.example.tetris.constants.FieldConstants
import com.example.tetris.helpers.array2dOfByte
import com.example.tetris.storage.AppPreferences
import com.example.tetris.views.NextBlockView

class Tetris {
    var score: Int = 0
    var lines: Int = 0
    private var preferences: AppPreferences? = null
    var currentBlock: Block? = null
    var nextBlock: Block? = null
    var currentState: String = Statuses.AWAITING_START.name
    private var field: Array<ByteArray>
            = array2dOfByte(FieldConstants.ROW_COUNT.value, FieldConstants.COLUMN_COUNT.value)
    private var nextBlockView: NextBlockView? = null

    enum class Statuses {
        AWAITING_START, ACTIVE, INACTIVE, OVER
    }

    enum class Motions {
        LEFT, RIGHT, DOWN, ROTATION
    }

    fun startGame() {
        if (!isGameActive()) {
            currentState = Statuses.ACTIVE.name
            generateCurrentBlock()
        }
    }

    fun endGame() {
        score = 0
        lines = 0
        currentState = Statuses.OVER.name
        nextBlockView?.setBlock(null)
    }

    fun restartGame() {
        resetModel()
        startGame()
    }

    fun setNextBlockView(view: NextBlockView) {
        nextBlockView = view
    }

    fun setPreferences(preferences: AppPreferences) {
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, col: Int): Byte? {
        return field[row][col]
    }

    fun setCellStatus(row: Int, col: Int, status: Byte?) {
        if (status != null) {
            field[row][col] = status
        }
    }

    fun isGameAwaitingStart(): Boolean {
        return currentState == Statuses.AWAITING_START.name
    }

    fun isGameActive(): Boolean {
        return currentState == Statuses.ACTIVE.name
    }

    fun isGameOver(): Boolean {
        return currentState == Statuses.OVER.name
    }

    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()

            var frameNumber: Int? = currentBlock?.frameNumber
            val coordinate: Point? = Point()

            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            when (action) {
                Motions.LEFT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.minus(1)
                }
                Motions.RIGHT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.plus(1)
                }
                Motions.DOWN.name -> {
                    coordinate?.y = currentBlock?.position?.y?.plus(1)
                }
                Motions.ROTATION.name -> {
                    frameNumber = frameNumber?.plus(1)

                    if (frameNumber != null) {
                        if (frameNumber >= currentBlock?.frameCount as Int) {
                            frameNumber = 0
                        }
                    }
                }
            }

            if (!moveValidation(coordinate as Point, frameNumber)) {
                moveBlock(currentBlock?.position as Point, currentBlock?.frameNumber as Int)

                if (Motions.DOWN.name == action) {
                    persistCellData()
                    assessField()
                    generateCurrentBlock()

                    if (!possibleNextBlock()) {
                        currentBlock = null
                        currentState = Statuses.OVER.name
                        resetField(false)
                    }
                }
            } else {
                if (frameNumber != null) {
                    moveBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun resetField(ephemeralCellOnly: Boolean = true) {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            (0 until FieldConstants.COLUMN_COUNT.value)
                .filter {
                    !ephemeralCellOnly || field[i][it] == CellConstants.EPHEMERAL.value
                }
                .forEach {
                    field[i][it] = CellConstants.EMPTY.value
                }
        }
    }

    private fun assessField() {
        for (i in field.indices) {
            var emptyCells = 0
            for (j in field[i].indices) {
                val status = getCellStatus(i, j)

                if (CellConstants.EMPTY.value == status) {
                    emptyCells++
                }
            }

            if (emptyCells == 0) {
                shiftRows(i)
                boostScore()
                incrementLines()
            }
        }
    }

    private fun moveBlock(position: Point, frameNumber: Int) {
        synchronized(field) {
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)

            if (shape != null) {
                for (i in shape.indices) {
                    for (j in shape[i].indices) {
                        val x = position.x + j
                        val y = position.y + i

                        if (CellConstants.EMPTY.value != shape[i][j]) {
                            field[y][x] = shape[i][j]
                        }
                    }
                }
            }
        }
    }

    private fun shiftRows(n: Int) {
        if (n > 0) {
            for (i in n - 1 downTo 0) {
                for (j in field[i].indices) {
                    setCellStatus(i + 1, j, getCellStatus(i, j))
                }
            }
        }

        for (j in field[0].indices) {
            setCellStatus(0, j, CellConstants.EMPTY.value)
        }
    }

    private fun persistCellData() {
        for (row in 0 until field.size) {
            for (col in 0 until field[row].size) {
                var status = getCellStatus(row, col)

                if (status == CellConstants.EPHEMERAL.value) {
                    status = currentBlock?.colorByte
                    setCellStatus(row, col, status)
                }
            }
        }
    }

    private fun generateNextBlock() {
        nextBlock = Block.createBlock()
        nextBlockView?.setBlock(nextBlock as Block)
    }

    private fun generateCurrentBlock() {
        currentBlock = if (nextBlock != null) {
            nextBlock
        } else {
            Block.createBlock()
        }
        generateNextBlock()
    }

    private fun possibleNextBlock(): Boolean {
        return moveValidation(currentBlock?.position as Point, currentBlock?.frameNumber)
    }

    private fun boostScore() {
        score += 10
        if (score > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(score)
        }
    }

    private fun incrementLines() {
        lines += 1
    }

    private fun canMoving(position: Point, shape: Array<ByteArray>): Boolean {
        return if (position.y < 0 || position.x < 0) {
            false
        } else if (position.y + shape.size > FieldConstants.ROW_COUNT.value) {
            false
        } else if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value) {
            false
        } else {
            for (i in 0 until shape.size) {
                for (j in 0 until shape[i].size) {
                    val x = position.x + j
                    val y = position.y + i

                    if (CellConstants.EMPTY.value != shape[i][j] && CellConstants.EMPTY.value != field[y][x]) {
                        return false
                    }
                }
            }
            true
        }
    }

    private fun moveValidation(position: Point, frameNumber: Int?): Boolean {
        val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber as Int)
        return canMoving(position, shape as Array<ByteArray>)
    }

    private fun resetModel() {
        score = 0
        lines = 0
        currentState = Statuses.AWAITING_START.name
        resetField()
    }
}
