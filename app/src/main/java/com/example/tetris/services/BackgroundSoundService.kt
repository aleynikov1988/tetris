package com.example.tetris.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.tetris.R

class BackgroundSoundService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val binder = SoundBinder()

    companion object {
        private const val VOLUME = 0.5f
    }

    inner class SoundBinder : Binder() {
        fun getService() = this@BackgroundSoundService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer?.setVolume(VOLUME, VOLUME)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    fun setVolume(value: Float) {
        mediaPlayer?.setVolume(value, value)
    }
}
