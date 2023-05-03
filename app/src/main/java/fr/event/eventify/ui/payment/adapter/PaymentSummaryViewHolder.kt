package fr.event.eventify.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.databinding.ItemSummaryBinding

class PaymentSummaryViewHolder private constructor(
    private val binding: ItemSummaryBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): PaymentSummaryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSummaryBinding.inflate(layoutInflater, parent, false)
            return PaymentSummaryViewHolder(binding)
        }
    }

    fun bind (participant: Participant) {
        binding.tvNum.text = participant.participantNumber
    }
}