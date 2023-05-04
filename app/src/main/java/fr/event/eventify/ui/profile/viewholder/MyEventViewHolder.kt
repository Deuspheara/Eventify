package fr.event.eventify.ui.profile.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import fr.event.eventify.R
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.databinding.ItemFeedBinding
import fr.event.eventify.databinding.ItemOrganisedEventBinding
import fr.event.eventify.ui.home.EventViewHolder

class MyEventViewHolder private constructor(
    private val binding: ItemOrganisedEventBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): MyEventViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrganisedEventBinding.inflate(layoutInflater, parent, false)
            return MyEventViewHolder(binding)
        }
    }

    fun bind(event : EventLight){
        binding.apply {
            tvEvenementName.text = event.name
            tvDateOrganisedEvent.text = event.date
            tvLocationOrganisedEvent.text = event.location
            imgEvenement.load(event.image){
                crossfade(true)
                crossfade(1000)
                placeholder(R.drawable.logo_gradient)
                error(R.drawable.logo_gradient)
            }
        }
    }
}