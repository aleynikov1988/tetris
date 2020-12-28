package com.example.tetris.models

import com.example.tetris.constants.FieldConstants as Constants
import com.example.tetris.helpers.array2dOfByte
import com.example.tetris.storage.AppPreferences

class AppModel {
    var score: Int = 0;
    private var preferences: AppPreferences? = null
    var currentBlock: Block? = null;
    var currentState: String = Statuses.AWAITING_START.name
    private var field: Array<ByteArray>
            = array2dOfByte(Constants.ROW_COUNT.value, Constants.COLUMN_COUNT.value)

    enum class Statuses {
        AWAITING_START, ACTIVE, INACTIVE, OVER
    }

    enum class Motions {
        LEFT, RIGHT, DOWN, ROTATION
    }

    fun setPreferences(preferences: AppPreferences) {
        this.preferences = preferences
    }

    fun setCellStatus(row: Int, column: Int, status: Byte) {
        field[row][column] = status
    }

    fun getCellStatus(row: Int, column: Int): Byte? {
        return field[row][column]
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

    private fun boostScore() {
        score += 10
        if (score > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(score)
        }
    }

    private fun generateNextBlock() {
        currentBlock = Block.createBlock()
    }
}
