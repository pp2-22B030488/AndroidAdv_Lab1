package com.example.android_advanced_lab1.music_player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.let { action ->
            val serviceIntent = Intent(context, MusicService::class.java).setAction(action)
            context?.startService(serviceIntent)
        }
    }
}
