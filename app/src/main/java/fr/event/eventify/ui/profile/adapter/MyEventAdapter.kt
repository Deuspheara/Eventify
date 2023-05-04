package fr.event.eventify.ui.profile.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.ui.profile.viewholder.MyEventViewHolder

class MyEventAdapter : RecyclerView.Adapter<MyEventViewHolder>()  {

        var eventList: ArrayList<EventLight> = arrayListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventViewHolder {
            return MyEventViewHolder.newInstance(parent)
        }

        override fun getItemCount(): Int {
            return eventList.size
        }

        override fun onBindViewHolder(holder: MyEventViewHolder, position: Int) {
            holder.bind(eventList[position])
        }

        override fun onViewRecycled(holder: MyEventViewHolder) {
            super.onViewRecycled(holder)
            holder.itemView.tag = null
        }
}