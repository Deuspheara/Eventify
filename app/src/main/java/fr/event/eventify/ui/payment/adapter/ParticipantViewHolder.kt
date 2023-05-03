package fr.event.eventify.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.databinding.ItemParticipantBinding

class ParticipantViewHolder private constructor(
    private val binding: ItemParticipantBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): ParticipantViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemParticipantBinding.inflate(layoutInflater, parent, false)
            return ParticipantViewHolder(binding)
        }
    }

    fun bind (participant: Participant) {
        binding.tvNum.text = participant.participantNumber
    }
}