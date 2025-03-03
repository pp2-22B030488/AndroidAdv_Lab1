package com.example.android_advanced_lab1.calendar_events

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class CalendarRepository(private val context: Context) {

    private val _events = MutableLiveData<List<CalendarEvent>>()
    val events: LiveData<List<CalendarEvent>> = _events

    fun fetchCalendarEvents() {
        val contentResolver: ContentResolver = context.contentResolver
        val calendarUri = CalendarContract.Events.CONTENT_URI

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART
        )

        val selection = "${CalendarContract.Events.DTSTART} >= ?"
        val selectionArgs = arrayOf(System.currentTimeMillis().toString())

        val cursor: Cursor? = contentResolver.query(
            calendarUri, projection, selection, selectionArgs, CalendarContract.Events.DTSTART + " ASC"
        )

        cursor?.use {
            val eventsList = mutableListOf<CalendarEvent>()
            val idIndex = it.getColumnIndex(CalendarContract.Events._ID)
            val titleIndex = it.getColumnIndex(CalendarContract.Events.TITLE)
            val startIndex = it.getColumnIndex(CalendarContract.Events.DTSTART)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val title = it.getString(titleIndex) ?: "No Title"
                val startTime = it.getLong(startIndex)

                eventsList.add(CalendarEvent(title, startTime))
            }
            _events.postValue(eventsList)
        }
    }
}
