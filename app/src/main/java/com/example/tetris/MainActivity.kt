package com.example.tetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tetris.storage.AppPreferences
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    var tvHighScore: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val btnNewGame = findViewById<Button>(R.id.btn_new_game)
        val btnResetScore = findViewById<Button>(R.id.btn_reset_score)
        val btnExit = findViewById<Button>(R.id.btn_exit)
        tvHighScore = findViewById(R.id.tv_high_score)

        btnNewGame.setOnClickListener(this::onClickBtnNewGame)
        btnResetScore.setOnClickListener(this::onClickBtnResetScore)
        btnExit.setOnClickListener(this::onClickBtnExit)
    }

    private fun onClickBtnNewGame(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun onClickBtnResetScore(view: View) {
        val preferences = AppPreferences(this)
        preferences.resetHighScore()
        Snackbar.make(view, "Score was reset", Snackbar.LENGTH_SHORT).show()
        tvHighScore?.text = "High score ${preferences.getHighScore()}"
    }

    private fun onClickBtnExit(view: View) {
        System.exit(0)
    }
}
