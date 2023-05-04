package fr.event.eventify.ui.profile_detail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.databinding.ItemTransactionBinding

class OrganisationEventDetailViewHolder private constructor(
    private val binding: ItemTransactionBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): OrganisationEventDetailViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemTransactionBinding.inflate(layoutInflater, parent, false)
            return OrganisationEventDetailViewHolder(binding)
        }
    }

    fun bind(transaction: Transaction) {
        binding.apply {
            val currency = if(transaction.currency == "EUR") "€" else transaction.currency


          transaction.amount?.let {
              if(it > 0.0){
                  tvAmountUserTransaction.text = "+${transaction.amount} $currency"
                    tvAmountUserTransaction.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
              }else{
                  tvAmountUserTransaction.text = "-${transaction.amount} $currency"
                  tvAmountUserTransaction.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
              }
          }
          tvNameUserTransaction.text = transaction.user?.toString()
        }
    }
}