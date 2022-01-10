package com.aitd.module_chat.rtc

import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXContext

object QXCallPlayer {

    private lateinit var mediaPlayer: MediaPlayer
    // 防止重复stop
    private var isPlaying = false

    fun start(context: Context) {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.tqxd_voip_call)
            mediaPlayer.start()
            mediaPlayer.isLooping = true
            isPlaying = true
            vibrate()
        }catch (ex:Exception) {
            ex.printStackTrace()
        }
    }

    fun stop(context: Context) {
        try {
            if (isPlaying && mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                isPlaying = false
                cancelVibrate()
            }
        }catch (ex: Exception) {
            ex.printStackTrace()
            mediaPlayer = MediaPlayer.create(context, R.raw.tqxd_voip_call)
        }
    }

    private fun vibrate() {
        val vibrator = QXContext.getInstance().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(longArrayOf(0L, 200L, 250L, 200L), 0)
    }

    private fun cancelVibrate() {
        val vibrator = QXContext.getInstance().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
}