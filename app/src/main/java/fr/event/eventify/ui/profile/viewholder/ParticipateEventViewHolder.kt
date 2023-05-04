package fr.event.eventify.ui.profile.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import fr.event.eventify.R
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.databinding.ItemOrganisedEventBinding
import fr.event.eventify.databinding.ItemParticipatedEventBinding

class ParticipateEventViewHolder private constructor(
    private val binding: ItemParticipatedEventBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): ParticipateEventViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemParticipatedEventBinding.inflate(layoutInflater, parent, false)
            return ParticipateEventViewHolder(binding)
        }
    }

    fun bind(event : EventLight, joinedEvent: RemoteUser.JoinedEvent){
        binding.apply {
            tvParticipatedName.text = event.name
            tvDateParticipatedEvent.text = event.date
            tvLocationOrganisedEvent.text = event.location
            imgParticipatedEvent.load(event.image){
                crossfade(true)
                crossfade(1000)
                placeholder(R.drawable.logo_gradient)
                error(R.drawable.logo_gradient)
            }
            tvNameParticitedEvent.text =
                "${joinedEvent.participant?.firstName} ${joinedEvent.participant?.lastName}"
            tvEmail.text = joinedEvent.participant?.email

        }
    }
}