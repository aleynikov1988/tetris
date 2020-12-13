package com.example.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tetris.storage.AppPreferences

class GameActivity : AppCompatActivity() {
    var preferences: AppPreferences? = null
    var tvCurrentScore: TextView? = null
    var tvHighScore: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        preferences = AppPreferences(this)

        tvCurrentScore = findViewById<TextView>(R.id.tv_current_score)
        tvHighScore = findViewById<TextView>(R.id.tv_high_score)
        val btnRestart = findViewById<Button>(R.id.btn_restart)

        btnRestart.setOnClickListener(this::onClickBtnRestart)

        updateCurrentScore()
        updateHighScore()
    }

    private fun onClickBtnRestart(view: View) {
        Toast.makeText(this, "Game is restarting...", Toast.LENGTH_SHORT).show()
    }

    private fun updateCurrentScore() {
        tvCurrentScore?.text = "0"
    }

    private fun updateHighScore() {
        tvHighScore?.text = preferences?.getHighScore().toString()
    }
}
