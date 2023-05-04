package fr.event.eventify.ui.profile_detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.ui.profile_detail.viewholder.OrganisationEventDetailViewHolder

class OrganisedEventDetailAdapter : RecyclerView.Adapter<OrganisationEventDetailViewHolder>() {

    private var transactions: ArrayList<Transaction> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganisationEventDetailViewHolder {
        return OrganisationEventDetailViewHolder.newInstance(parent)
    }

    override fun onBindViewHolder(holder: OrganisationEventDetailViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    fun submitList(list: List<Transaction>) {
        transactions.clear()
        transactions.addAll(list)
        notifyDataSetChanged()
    }

}