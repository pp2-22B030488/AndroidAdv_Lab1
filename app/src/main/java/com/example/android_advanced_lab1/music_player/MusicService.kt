package com.example.android_advanced_lab1.music_player

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.android_advanced_lab1.R
import java.io.IOException
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "MusicServiceChannel"
    private val trackList = listOf("we_are.mp3", "blue_bird.mp3", "hxh_opening_departure.mp3")
    private var currentTrackIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private val trackBackgrounds = mapOf(
        "we_are.mp3" to R.drawable.one_piece,
        "blue_bird.mp3" to R.drawable.naruto,
        "hxh_opening_departure.mp3" to R.drawable.hunter_x_hunter
    )


    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    sendMusicState(true)
                }
            }
            handler.postDelayed(this, 500) // Обновляем каждые 500 мс
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> startMusic()
            "PAUSE" -> pauseMusic()
            "STOP" -> stopMusic()
            "NEXT" -> nextTrack()
            "PREVIOUS" -> previousTrack()
            "SEEK" -> {
                val seekTo = intent.getIntExtra("SEEK_TO", 0)
                mediaPlayer?.let {
                    if (it.isPlaying || it.currentPosition != 0) {
                        it.seekTo(seekTo)
                        sendMusicState(it.isPlaying)
                    }
                }
            }
            "GET_STATE" -> sendMusicState(mediaPlayer?.isPlaying == true)
        }
        return START_STICKY
    }

    private fun startMusic() {
        if (mediaPlayer == null) {
            playTrack(trackList[currentTrackIndex])

        } else {
            mediaPlayer?.start()
        }
        sendMusicState(true)
        showNotification()
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        sendMusicState(false)
        showNotification()
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateSeekBarRunnable) // Останавливаем обновление SeekBar
        sendMusicState(false)
        stopForeground(true)
        stopSelf()
    }


    private fun nextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % trackList.size
        playTrack(trackList[currentTrackIndex])
    }

    private fun previousTrack() {
        currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else trackList.size - 1
        playTrack(trackList[currentTrackIndex])
    }

    private fun playTrack(track: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.reset() // Сброс перед загрузкой нового трека

        try {
            val assetManager = assets
            val descriptor = assetManager.openFd(track)
            mediaPlayer?.setDataSource(
                descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length
            )
            descriptor.close()

            mediaPlayer?.setOnCompletionListener { nextTrack() }
            mediaPlayer?.setOnErrorListener { _, _, _ ->
                stopSelf()
                true
            }

            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener {
                it.start()
                handler.post(updateSeekBarRunnable) // Запускаем обновление SeekBar
                sendMusicState(true)
            }

        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }

        showNotification()
    }


    private fun sendMusicState(isPlaying: Boolean) {
        mediaPlayer?.let {
            val backgroundResId = trackBackgrounds[trackList[currentTrackIndex]] ?: R.drawable.one_piece
            val intent = Intent("MUSIC_STATE_CHANGED")
            intent.putExtra("IS_PLAYING", isPlaying)
            intent.putExtra("CURRENT_POSITION", it.currentPosition)
            intent.putExtra("DURATION", it.duration)
            intent.putExtra("EXTRA_BACKGROUND", backgroundResId)

            Log.d("MusicService", "Отправка LocalBroadcast")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }




    private fun showNotification() {
        val isPlaying = mediaPlayer?.isPlaying == true

        val playPauseIntent = Intent(this, MusicReceiver::class.java).setAction(if (isPlaying) "PAUSE" else "START")
        val playPausePendingIntent = PendingIntent.getBroadcast(this, 5, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val prevIntent = Intent(this, MusicReceiver::class.java).setAction("PREVIOUS")
        val pendingPrev = PendingIntent.getBroadcast(this, 6, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, MusicReceiver::class.java).setAction("NEXT")
        val pendingNext = PendingIntent.getBroadcast(this, 7, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(this, MusicReceiver::class.java).setAction("STOP")
        val pendingStop = PendingIntent.getBroadcast(this, 8, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing: ${trackList[currentTrackIndex]}")
            .setSmallIcon(R.drawable.ic_music)
            .addAction(R.drawable.ic_previous, "Previous", pendingPrev)
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(R.drawable.ic_next, "Next", pendingNext)
            .addAction(R.drawable.ic_pause, "Stop", pendingStop)
            .setOngoing(isPlaying)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Music Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        handler.removeCallbacks(updateSeekBarRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
