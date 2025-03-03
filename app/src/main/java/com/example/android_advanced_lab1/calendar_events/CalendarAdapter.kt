import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_advanced_lab1.R
import com.example.android_advanced_lab1.calendar_events.CalendarEvent
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private val events: List<CalendarEvent>) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventIcon: ImageView = view.findViewById(R.id.eventIcon)
        val eventTitle: TextView = view.findViewById(R.id.eventTitle)
        val eventDate: TextView = view.findViewById(R.id.eventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]

        // Устанавливаем заголовок события
        holder.eventTitle.text = event.title

        // Форматируем дату
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        holder.eventDate.text = dateFormat.format(Date(event.startTime))

        // Устанавливаем иконку в зависимости от события (можно улучшить)
        if (event.title.lowercase().contains("с днем рождения!")) {
            holder.eventIcon.setImageResource(R.drawable.ic_cake)
        } else {
            holder.eventIcon.setImageResource(R.drawable.ic_calendar)
        }
    }

    override fun getItemCount() = events.size
}
