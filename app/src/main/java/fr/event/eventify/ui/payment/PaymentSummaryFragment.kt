package fr.event.eventify.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import fr.event.eventify.databinding.FragmentPaymentSummaryBinding
import fr.event.eventify.databinding.ItemSummaryBinding

class PaymentSummaryFragment : Fragment() {

    private lateinit var binding: FragmentPaymentSummaryBinding
    private var numberOfParticipant = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentSummaryBinding.inflate(inflater, container, false)
        arguments?.let {
            numberOfParticipant = it.getInt("PARTICIPANT")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPrevious.setOnClickListener {
            findNavController().popBackStack()
        }

        for (i in 1 until numberOfParticipant + 1) {
            val myLayout = ItemSummaryBinding.inflate(layoutInflater, null, false)
            binding.linearLayoutParticipant.addView(myLayout.root)
        }

    }
}