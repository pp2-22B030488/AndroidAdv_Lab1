package com.example.android_advanced_lab1.music_player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android_advanced_lab1.R
import java.util.concurrent.TimeUnit

class ServiceFragment : Fragment() {
    private lateinit var btnStartStop: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_service, container, false)

        btnStartStop = view.findViewById(R.id.btnStartStop)
        btnNext = view.findViewById(R.id.btnNext)
        btnPrev = view.findViewById(R.id.btnPrev)
        seekBar = view.findViewById(R.id.seekBar)
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime)
        tvTotalTime = view.findViewById(R.id.tvTotalTime)

        btnStartStop.setOnClickListener {
            val action = if (isPlaying) "PAUSE" else "START"

            // Отправляем команду в сервис
            val intent = Intent(requireContext(), MusicService::class.java).setAction(action)
            requireContext().startService(intent)

            // Обновляем UI после отправки команды
            isPlaying = !isPlaying
            btnStartStop.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        }


        btnNext.setOnClickListener {

            val intent = Intent(requireContext(), MusicService::class.java).setAction("NEXT")
            requireContext().startService(intent)
        }

        btnPrev.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java).setAction("PREVIOUS")
            requireContext().startService(intent)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) { // Только если пользователь изменил положение SeekBar
                    val intent = Intent(context, MusicService::class.java)
                    intent.action = "SEEK"
                    intent.putExtra("SEEK_TO", progress)
                    context?.startService(intent)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })



        return view
    }
    private val musicStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MusicReceiver", "Broadcast received") // Проверяем, вызывается ли он
            intent?.let {
                val isPlaying = it.getBooleanExtra("IS_PLAYING", false)
                val currentPosition = it.getIntExtra("CURRENT_POSITION", 0)
                val duration = it.getIntExtra("DURATION", 0)
                val backgroundResId = it.getIntExtra("EXTRA_BACKGROUND", R.drawable.one_piece)

                seekBar.max = duration
                seekBar.progress = currentPosition

                // Меняем обложку
                view?.findViewById<ImageView>(R.id.musicSlide)?.setImageResource(backgroundResId)

                // Меняем иконку Play/Pause
                btnStartStop.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
            }
        }
    }



    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("MUSIC_STATE_CHANGED")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(musicStateReceiver, filter)
    }
    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(musicStateReceiver)
    }




    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                // Запрашиваем текущее состояние музыки
                val intent = Intent(requireContext(), MusicService::class.java).setAction("GET_STATE")
                requireContext().startService(intent)
            }
            handler.postDelayed(this, 500) // Обновляем каждые 500 мс
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
