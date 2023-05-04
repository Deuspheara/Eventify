package fr.event.eventify.ui.payment.adapter

import android.provider.Telephony.Mms.Part
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.local.Participant


class ParticipantAdapter : RecyclerView.Adapter<ParticipantViewHolder>()  {

    var participantList: ArrayList<Participant> = arrayListOf()
    private var currentUser : Participant = Participant()

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

    fun updateCurrentUser(participant: Participant){
        currentUser = participant
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(participantList[position], position, currentUser)
        holder.itemView.tag = position
    }

    override fun onViewRecycled(holder: ParticipantViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.tag = null
    }

}