package fr.event.eventify.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.databinding.ItemOrganisedEventBinding
import fr.event.eventify.ui.profile.viewholder.ParticipateEventViewHolder
import javax.inject.Inject

class ParticipateEventAdapter : RecyclerView.Adapter<ParticipateEventViewHolder>() {

    private var events: ArrayList<EventLight> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipateEventViewHolder {
        return ParticipateEventViewHolder.newInstance(parent)
    }

    override fun onBindViewHolder(holder: ParticipateEventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    fun submitList(list: List<EventLight>) {
        events.clear()
        events.addAll(list)
        notifyDataSetChanged()
    }

}