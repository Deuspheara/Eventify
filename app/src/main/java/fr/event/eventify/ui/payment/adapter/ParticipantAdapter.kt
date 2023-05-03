package fr.event.eventify.ui.payment.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.local.Participant


class ParticipantAdapter : RecyclerView.Adapter<ParticipantViewHolder>()  {

    val participantList: ArrayList<Participant> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val viewHolder = ParticipantViewHolder.newInstance(parent)
        viewHolder.onTextChangedListener = object : OnTextChangedListener {
            override fun onTextChanged(position: Int, participant: Participant) {
                participantList[position] = participant
                Log.d("ParticipantAdapter", "onTextChanged: $participantList")
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return participantList.size
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(participantList[position], position)
    }

}