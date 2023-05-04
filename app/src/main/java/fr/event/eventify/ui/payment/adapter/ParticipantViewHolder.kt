package fr.event.eventify.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.databinding.ItemParticipantBinding
import java.text.FieldPosition

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


    var onTextChangedListener: OnTextChangedListener? = null

    fun bind(participant: Participant, position: Int) {
        binding.apply {
            tvNum.text = "Participant ${position + 1}"
            inputFirstName.setText(participant.firstName)
            inputLastName.setText(participant.lastName)
            inputEmail.setText(participant.email)

            tvNum.addTextChangedListener {
                onTextChangedListener?.onTextChanged(position, participant)
            }
            inputFirstName.addTextChangedListener {
                participant.firstName = it.toString()
                onTextChangedListener?.onTextChanged(position, participant)
            }
            inputLastName.addTextChangedListener {
                participant.lastName = it.toString()
                onTextChangedListener?.onTextChanged(position, participant)
            }
            inputEmail.addTextChangedListener {
                participant.email = it.toString()
                onTextChangedListener?.onTextChanged(position, participant)
            }
        }
    }

}