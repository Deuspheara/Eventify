package fr.event.eventify.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import coil.load
import fr.event.eventify.R
import fr.event.eventify.core.mapper.EventMapper
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.databinding.ItemFeedBinding
import fr.event.eventify.ui.event.EventDetailsActivity
import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat
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
        val isCountParticipantNull = event.participants?.count() == null

        binding.apply {
            tvName.text = event.name
            tvCategory.text = event.categoryEvent?.name
            tvHour.text = event.date?.let {
                // jeudi 1 janvier 12h00 in french
                val date = LocalDateTime.ofInstant(it.toDate().toInstant(), ZoneId.systemDefault())
                val frenchDay = when (date.dayOfWeek.toString()) {
                    "MONDAY" -> "Lundi"
                    "TUESDAY" -> "Mardi"
                    "WEDNESDAY" -> "Mercredi"
                    "THURSDAY" -> "Jeudi"
                    "FRIDAY" -> "Vendredi"
                    "SATURDAY" -> "Samedi"
                    "SUNDAY" -> "Dimanche"
                    else -> "Lundi"
                }
                val frenchMonth = when (date.month.toString()) {
                    "JANUARY" -> "Janvier"
                    "FEBRUARY" -> "Février"
                    "MARCH" -> "Mars"
                    "APRIL" -> "Avril"
                    "MAY" -> "Mai"
                    "JUNE" -> "Juin"
                    "JULY" -> "Juillet"
                    "AUGUST" -> "Août"
                    "SEPTEMBER" -> "Septembre"
                    "OCTOBER" -> "Octobre"
                    "NOVEMBER" -> "Novembre"
                    "DECEMBER" -> "Décembre"
                    else -> "Janvier"
                }
                "$frenchDay ${date.dayOfMonth} $frenchMonth ${date.hour}h${date.minute}"



            }
            tvPlace.text = event.location?.name
            tvNbPeople.text = (if (isCountParticipantNull) "0" else event.participants?.count().toString()) + '/' + event.nbTickets.toString()
            tvPrice.text = event.ticketPrice?.amount.toString() + "€"
            ivEvent.load(event.image){
                placeholder(R.drawable.app_icon)
                error(R.drawable.app_icon)
            }

            constraintLayoutItemFeed.setOnClickListener{
                //launch Home Activity
                val intent = Intent(it.context, EventDetailsActivity::class.java)
                val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                val bundle: Bundle = bundleOf("event" to EventMapper().mapToEventLight(event))

                intent.putExtras(bundle)

                it.context.startActivity(intent)
            }

        }
    }
}