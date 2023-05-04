package fr.event.eventify.ui.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.ui.home.FavoriteViewHolder

class FavoriteAdapter(
    private val userId: String,
    private val onClickFavorite: (String, Boolean) -> Unit
) : RecyclerView.Adapter<FavoriteViewHolder>() {

    var eventList: ArrayList<Event> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder.newInstance(parent)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {

        holder.bind(eventList[position], userId, onClickFavorite)
    }

    fun submitList(list: List<Event>) {
        eventList.clear()
        eventList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: FavoriteViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.tag = null
    }
}