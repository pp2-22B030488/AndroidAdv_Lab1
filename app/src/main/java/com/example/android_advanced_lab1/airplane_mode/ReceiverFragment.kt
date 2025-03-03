package com.example.android_advanced_lab1.airplane_mode

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android_advanced_lab1.R
import com.example.android_advanced_lab1.calendar_events.ReceiverViewModel

class ReceiverFragment : Fragment() {

    private val viewModel: ReceiverViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_receiver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.textViewAirplaneMode)
        val imageView = view.findViewById<ImageView>(R.id.imageViewAirplaneMode)

        viewModel.airplaneModeState.observe(viewLifecycleOwner) { isAirplaneModeOn ->
            textView.text = if (isAirplaneModeOn) "Airplane Mode: ON"
            else "Airplane Mode: OFF"
            if (isAirplaneModeOn) {
                imageView.setImageResource(R.drawable.ic_airplanemode_active)
            }
            else{
                imageView.setImageResource(R.drawable.ic_airplanemode_inactive)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            AirplaneModeReceiver,
            IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(AirplaneModeReceiver)
    }
}
