package fr.event.eventify.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import fr.event.eventify.databinding.FragmentTicketInformationBinding

class TicketInformationFragment : Fragment() {

    private lateinit var binding: FragmentTicketInformationBinding
    private var numberOfTicket = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTicketInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTicket.text = numberOfTicket.toString()

        binding.btPlus.setOnClickListener {
            numberOfTicket++
            binding.tvTicket.text = numberOfTicket.toString()
        }

        binding.btMinus.setOnClickListener {
            if (numberOfTicket > 1) {
                numberOfTicket--
                binding.tvTicket.text = numberOfTicket.toString()
            }
        }

        binding.btReturn.setOnClickListener {
            activity?.finish()
        }

        binding.buttonNext.setOnClickListener {
            val action = TicketInformationFragmentDirections.actionTicketInformationFragmentToParticipantFragment()
            findNavController().navigate(action.actionId, bundleOf("PARTICIPANT" to numberOfTicket))
        }
    }

}