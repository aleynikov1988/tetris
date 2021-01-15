package com.example.tetris.activities

import android.annotation.SuppressLint
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tetris.R
import com.example.tetris.models.Tetris
import com.example.tetris.storage.AppPreferences
import com.example.tetris.views.NextBlockView
import com.example.tetris.views.TetrisView

class GameActivity : AppCompatActivity() {
    var preferences: AppPreferences? = null
    var tvCurrentScore: TextView? = null
    var tvHighScore: TextView? = null

    private lateinit var tetrisView: TetrisView
    private lateinit var nextBlockView: NextBlockView
    private val tetrisModel: Tetris = Tetris()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        preferences = AppPreferences(this)
        tetrisModel.setPreferences(preferences as AppPreferences)

        tvCurrentScore = findViewById<TextView>(R.id.tv_current_score)
        tvHighScore = findViewById<TextView>(R.id.tv_high_score)
        tetrisView = findViewById<TetrisView>(R.id.view_tetris)
        nextBlockView = findViewById<NextBlockView>(R.id.view_next_block)

        tetrisView.setGameActivity(this)
        tetrisView.setTetrisModel(tetrisModel)
        tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        tetrisModel.setNextBlockView(nextBlockView)

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
        if (tetrisModel.isGameOver() || tetrisModel.isGameAwaitingStart()) {
            tetrisModel.startGame()
            tetrisView.setGameCommandWithDelay(Tetris.Motions.DOWN)
        }
        else if (tetrisModel.isGameActive()) {
            when (resolveTouchDirection(view, event)) {
                0 -> moveTetramino(Tetris.Motions.LEFT)
                1 -> moveTetramino(Tetris.Motions.ROTATION)
                2 -> moveTetramino(Tetris.Motions.DOWN)
                3 -> moveTetramino(Tetris.Motions.RIGHT)
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

    private fun moveTetramino(motion: Tetris.Motions) {
        if (tetrisModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }

    private fun onClickBtnMotion(view: View) {
        when (view.id) {
            R.id.btn_motion_left -> moveTetramino(Tetris.Motions.LEFT)
            R.id.btn_motion_down -> moveTetramino(Tetris.Motions.DOWN)
            R.id.btn_motion_right -> moveTetramino(Tetris.Motions.RIGHT)
            R.id.btn_motion_rotation -> moveTetramino(Tetris.Motions.ROTATION)
        }

//        val rectf = Rect()
//        val rectf2 = Rect()
//        val rectf3 = Rect()
//
//        val fieldLayout = findViewById<LinearLayout>(R.id.layout_field)
//        val infoLayout = findViewById<LinearLayout>(R.id.layout_info)
//
//        tetrisView.getLocalVisibleRect(rectf)
//        fieldLayout.getLocalVisibleRect(rectf2)
//        infoLayout.getLocalVisibleRect(rectf2)
//
//
//
////        var location = intArrayOf(0, 1)
////        tetrisView.getLocationOnScreen(location)
////
//        Log.d("tetrisView", "x: ${rectf.left}")
//        Log.d("tetrisView", "y: ${rectf.top}")
//        Log.d("tetrisView", "w: ${rectf.width()}")
//        Log.d("tetrisView", "h: ${rectf.height()}")
//
//        Log.d("fieldLayout", "x: ${rectf2.left}")
//        Log.d("fieldLayout", "y: ${rectf2.top}")
//        Log.d("fieldLayout", "w: ${rectf2.width()}")
//        Log.d("fieldLayout", "h: ${rectf2.height()}")
//
//        Log.d("infoLayout", "x: ${rectf3.left}")
//        Log.d("infoLayout", "y: ${rectf3.top}")
//        Log.d("infoLayout", "w: ${rectf3.width()}")
//        Log.d("infoLayout", "h: ${rectf3.height()}")


//        val infoLayout = findViewById<LinearLayout>(R.id.layout_info)
//        infoLayout.setPadding(0, location[1], 0, 0)
    }

    fun updateCurrentScore(score: Int = 0) {
        tvCurrentScore?.text = score.toString()
    }

    fun updateHighScore() {
        tvHighScore?.text = preferences?.getHighScore().toString()
    }
}
