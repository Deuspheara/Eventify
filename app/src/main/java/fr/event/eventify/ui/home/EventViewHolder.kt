package fr.event.eventify.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import fr.event.eventify.R
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.databinding.ItemFeedBinding
import java.time.LocalDateTime
import java.time.ZoneId


class EventViewHolder private constructor(
    private val binding: ItemFeedBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): EventViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemFeedBinding.inflate(layoutInflater, parent, false)
            return EventViewHolder(binding)
        }
    }

    fun bind(event: Event) {


        binding.apply {
            tvName.text = event.name
            tvCategory.text = event.categoryEvent?.name
            tvHour.text = "11h11"
            tvPlace.text = event.location?.name
            tvDate.text = "11/11"
            tvPrice.text = event.ticketPrice?.amount.toString() + "€"
            ivEvent.load(event.image){
                placeholder(R.drawable.app_icon)
                error(R.drawable.app_icon)
            }

        }
    }
}