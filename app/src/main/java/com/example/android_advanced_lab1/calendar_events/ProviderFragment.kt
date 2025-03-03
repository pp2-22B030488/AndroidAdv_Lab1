package com.example.android_advanced_lab1.calendar_events

import CalendarAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_advanced_lab1.R
import java.text.SimpleDateFormat
import java.util.*

class ProviderFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_provider, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        checkCalendarPermission()
        return view
    }

    private fun checkCalendarPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CALENDAR), 100)
        } else {
            fetchCalendarEvents()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCalendarEvents()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCalendarEvents() {
        val events = mutableListOf<CalendarEvent>() // Изменяем список на правильный тип
        val uri: Uri = CalendarContract.Events.CONTENT_URI
        val projection = arrayOf(CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART)

        val cursor: Cursor? = requireActivity().contentResolver.query(uri, projection, null, null, CalendarContract.Events.DTSTART + " ASC")
        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(0) ?: "No Title"
                val timestamp = it.getLong(1)

                // Добавляем объект CalendarEvent вместо строки
                events.add(CalendarEvent(title, timestamp))
            }
        }

        if (events.isEmpty()) {
            events.add(CalendarEvent("No upcoming events", System.currentTimeMillis()))
        }

        recyclerView.adapter = CalendarAdapter(events) // Теперь передаём правильный тип
    }

}
