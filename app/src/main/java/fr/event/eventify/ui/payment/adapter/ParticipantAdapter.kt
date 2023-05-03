package fr.event.eventify.ui.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.local.Participant

class ParticipantAdapter : RecyclerView.Adapter<ParticipantViewHolder>()  {

    val participantList: ArrayList<Participant> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        return ParticipantViewHolder.newInstance(parent)
    }

    override fun getItemCount(): Int {
        return participantList.size
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(participantList[position])
    }

}