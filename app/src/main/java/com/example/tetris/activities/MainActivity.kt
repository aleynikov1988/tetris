package com.example.tetris.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tetris.R
import com.example.tetris.services.BackgroundSoundService
import com.example.tetris.storage.AppPreferences

class MainActivity : AppCompatActivity() {
    var tvHighScore: TextView? = null
    var preferences: AppPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        playBackgroundSound()

        preferences = AppPreferences(this)
        tvHighScore = findViewById(R.id.tv_high_score)
        tvHighScore?.text = "High score: ${preferences?.getHighScore()}"

        val btnNewGame = findViewById<Button>(R.id.btn_new_game)
        val btnContinue = findViewById<Button>(R.id.btn_continue)
        val btnExit = findViewById<Button>(R.id.btn_exit)

        btnNewGame.setOnClickListener(this::onClickBtnNewGame)
        btnContinue.setOnClickListener(this::onClickBtnContinue)
        btnExit.setOnClickListener(this::onClickBtnExit)

        if (preferences?.getHighScore() != 0) {
            btnContinue.visibility = View.VISIBLE
        }
    }

    private fun start() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun playBackgroundSound() {
        val intent = Intent(this, BackgroundSoundService::class.java)
        startService(intent)
    }

    private fun onClickBtnNewGame(view: View) {
        preferences?.resetHighScore()
        tvHighScore?.text = "High score: ${preferences?.getHighScore()}"

        start()
    }

    private fun onClickBtnContinue(view: View) {
        start()
    }

    private fun onClickBtnExit(view: View) {
        System.exit(0)
    }
}
