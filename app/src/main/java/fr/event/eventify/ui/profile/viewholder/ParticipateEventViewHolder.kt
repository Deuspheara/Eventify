package fr.event.eventify.ui.profile.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import fr.event.eventify.R
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.databinding.ItemOrganisedEventBinding

class ParticipateEventViewHolder private constructor(
    private val binding: ItemOrganisedEventBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): ParticipateEventViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrganisedEventBinding.inflate(layoutInflater, parent, false)
            return ParticipateEventViewHolder(binding)
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