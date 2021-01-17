package com.example.tetris.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.tetris.R
import com.example.tetris.storage.AppPreferences

class StatisticsActivity : AppCompatActivity() {
    var preferences: AppPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        preferences = AppPreferences(this)

        val tvHighScope = findViewById<TextView>(R.id.tv_high_score)
        tvHighScope.text = "${preferences?.getHighScore()}"
    }
}
