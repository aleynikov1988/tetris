package com.example.tetris.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tetris.R
import com.example.tetris.services.BackgroundSoundService
import com.example.tetris.storage.AppPreferences
import info.androidhive.fontawesome.FontTextView

class MainActivity : AppCompatActivity() {
    var tvHighScore: TextView? = null
    var preferences: AppPreferences? = null

    private lateinit var soundService: BackgroundSoundService
    private var ssBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BackgroundSoundService.SoundBinder
            soundService = binder.getService()
            ssBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            ssBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

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

        val soundOnView = findViewById<FontTextView>(R.id.sound_on)
        val soundOffView = findViewById<FontTextView>(R.id.sound_off)

        soundOnView.setOnClickListener(this::onClickSoundIcon)
        soundOffView.setOnClickListener(this::onClickSoundIcon)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, BackgroundSoundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        ssBound = false
    }

    private fun start() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
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

    private fun onClickSoundIcon(view: View) {
        when (view.id) {
            R.id.sound_on -> {
                findViewById<FontTextView>(R.id.sound_off).visibility = View.VISIBLE
                if (ssBound) soundService.setVolume(0f)
            }
            R.id.sound_off -> {
                findViewById<FontTextView>(R.id.sound_on).visibility = View.VISIBLE
                if (ssBound) soundService.setVolume(0.5f)
            }
        }
        view.visibility = View.GONE
    }
}
