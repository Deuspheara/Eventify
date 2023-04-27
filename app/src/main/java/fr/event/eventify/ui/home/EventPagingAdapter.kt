package fr.event.eventify.ui.home

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import fr.event.eventify.core.models.event.remote.Event

class EventPagingAdapter : PagingDataAdapter<Event, EventViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder.newInstance(parent)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return )
    }
}