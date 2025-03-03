package com.example.android_advanced_lab1.calendar_events

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.android_advanced_lab1.airplane_mode.AirplaneModeReceiver

class ReceiverViewModel : ViewModel() {
    val airplaneModeState: LiveData<Boolean> = AirplaneModeReceiver.airplaneModeState
}
