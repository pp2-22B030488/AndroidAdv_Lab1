package com.example.android_advanced_lab1.airplane_mode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object AirplaneModeReceiver : BroadcastReceiver() {

    private val _airplaneModeState = MutableLiveData<Boolean>()
    val airplaneModeState: LiveData<Boolean> = _airplaneModeState

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val isAirplaneModeOn = Settings.Global.getInt(
                context?.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON, 0
            ) != 0
            _airplaneModeState.postValue(isAirplaneModeOn)
        }
    }
}
