package com.example.tetris.views

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.tetris.GameActivity
import com.example.tetris.models.AppModel

class TetrisView : View {
    private var paint = Paint()
    private var lastMove: Long = 0
    private var model: AppModel? = null
    private var activity: GameActivity? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    companion object {
        private val DELAY = 500
        private val BLOCK_OFFSET = 2
        private val FRAME_OFFSET_BASE = 10
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
                invalidate()
                return
            }
        }
    }

    fun setGameCommandwWithDelay(cmd: AppModel.Motions) {
        val now = System.currentTimeMillis()

        if (now - lastMove > DELAY) {
            model?.generateField(cmd.name)
            invalidate()
            lastMove = now
        }
        updateScores()
    }

    private fun updateScores() {
        activity?.updateCurrentScore(model?.score as Int)
        activity?.updateHighScore()
    }
}
