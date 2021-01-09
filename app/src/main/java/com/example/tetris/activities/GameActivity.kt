package com.example.tetris.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tetris.R
import com.example.tetris.models.AppModel
import com.example.tetris.storage.AppPreferences
import com.example.tetris.views.NextBlockView
import com.example.tetris.views.TetrisView

class GameActivity : AppCompatActivity() {
    var preferences: AppPreferences? = null
    var tvCurrentScore: TextView? = null
    var tvHighScore: TextView? = null

    private lateinit var tetrisView: TetrisView
    private lateinit var nextBlockView: NextBlockView
    private val appModel: AppModel = AppModel()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        preferences = AppPreferences(this)
        appModel.setPreferences(preferences as AppPreferences)

        tvCurrentScore = findViewById<TextView>(R.id.tv_current_score)
        tvHighScore = findViewById<TextView>(R.id.tv_high_score)
        tetrisView = findViewById<TetrisView>(R.id.view_tetris)

        tetrisView.setActivity(this)
        tetrisView.setModel(appModel)
        tetrisView.setOnTouchListener(this::onTetrisViewTouch)

        nextBlockView = findViewById<NextBlockView>(R.id.view_next_block)
        nextBlockView.setModel(appModel)

        val btnMotionLeft = findViewById<Button>(R.id.btn_motion_left)
        val btnMotionDown = findViewById<Button>(R.id.btn_motion_down)
        val btnMotionRight = findViewById<Button>(R.id.btn_motion_right)
        val btnMotionRotation = findViewById<Button>(R.id.btn_motion_rotation)

        btnMotionLeft.setOnClickListener(this::onClickBtnMotion)
        btnMotionDown.setOnClickListener(this::onClickBtnMotion)
        btnMotionRight.setOnClickListener(this::onClickBtnMotion)
        btnMotionRotation.setOnClickListener(this::onClickBtnMotion)

        updateCurrentScore()
        updateHighScore()
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent): Boolean {
        if (appModel.isGameOver() || appModel.isGameAwaitingStart()) {
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        }
        else if (appModel.isGameActive()) {
            when (resolveTouchDirection(view, event)) {
                0 -> moveTetramino(AppModel.Motions.LEFT)
                1 -> moveTetramino(AppModel.Motions.ROTATION)
                2 -> moveTetramino(AppModel.Motions.DOWN)
                3 -> moveTetramino(AppModel.Motions.RIGHT)
            }
        }
        return true
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height

        return if (y > x) {
            if (x > 1 - y) 2 else 0
        } else {
            if (x > 1 - y) 3 else 1
        }
    }

    private fun moveTetramino(motion: AppModel.Motions) {
        if (appModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }

    private fun onClickBtnMotion(view: View) {
        when (view.id) {
            R.id.btn_motion_left -> moveTetramino(AppModel.Motions.LEFT)
            R.id.btn_motion_down -> moveTetramino(AppModel.Motions.DOWN)
            R.id.btn_motion_right -> moveTetramino(AppModel.Motions.RIGHT)
            R.id.btn_motion_rotation -> moveTetramino(AppModel.Motions.ROTATION)
        }
    }

    fun updateCurrentScore(score: Int = 0) {
        tvCurrentScore?.text = score.toString()
    }

    fun updateHighScore() {
        tvHighScore?.text = preferences?.getHighScore().toString()
    }
}
