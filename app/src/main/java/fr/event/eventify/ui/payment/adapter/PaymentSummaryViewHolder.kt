package fr.event.eventify.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.databinding.ItemSummaryBinding

interface OnTextChangedListener {
    fun onTextChanged(position: Int, participant: Participant)
}

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



    fun bind (participant: Participant, position: Int) {
        binding.apply {
            tvNum.text = "Participant ${position + 1}"
            tvFirstNameSummary.text = participant.firstName
            tvLastNameSummary.text = participant.lastName
            tvEmailSummary.text = participant.email
        }
    }
}