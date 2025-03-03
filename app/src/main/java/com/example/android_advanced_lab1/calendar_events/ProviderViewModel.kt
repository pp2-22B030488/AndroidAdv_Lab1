package com.example.android_advanced_lab1.calendar_events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class ProviderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalendarRepository(application)

    val events: LiveData<List<CalendarEvent>> = repository.events

    fun loadEvents() {
        repository.fetchCalendarEvents()
    }
}
