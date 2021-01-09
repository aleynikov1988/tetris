package com.example.tetris.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.tetris.R

class BackgroundSoundService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val VOLUME = 0.5f

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer?.setVolume(VOLUME, VOLUME)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer?.start()
        return startId
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}
