package com.example.tetris.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(ctx: Context) {
    var data: SharedPreferences = ctx.getSharedPreferences("tetris", Context.MODE_PRIVATE)

    fun saveHighScore(score: Int) {
        data.edit().putInt("high_score", score).apply()
    }

    fun getHighScore(): Int {
        return data.getInt("high_score", 0)
    }

    fun resetHighScore() {
        data.edit().putInt("high_score", 0).apply()
    }

    fun isSoundEnabled(): Boolean {
        return data.getBoolean("sound_status", true)
    }

    fun setSoundMode(enabled: Boolean) {
        data.edit().putBoolean("sound_status", enabled).apply()
    }
}
