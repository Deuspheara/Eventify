package fr.event.eventify.ui.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.databinding.FragmentTicketInformationBinding

@AndroidEntryPoint
class TicketInformationFragment : Fragment() {

    private lateinit var binding: FragmentTicketInformationBinding
    private var numberOfTicket = 1
    private val viewModel: TicketInformationViewModel by activityViewModels()
    private var currentEvent : EventLight? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTicketInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTicket.text = numberOfTicket.toString()

        binding.btPlus.setOnClickListener {
            numberOfTicket++
            binding.tvTicket.text = numberOfTicket.toString()
            binding.tvTotalTicket.text = (currentEvent?.ticketPrice?.times(numberOfTicket)).let { String.format("%.2f", it) } + "€"
        }

        binding.btMinus.setOnClickListener {
            if (numberOfTicket > 1) {
                numberOfTicket--
                binding.tvTicket.text = numberOfTicket.toString()
            }
            binding.tvTotalTicket.text = (currentEvent?.ticketPrice?.times(numberOfTicket)).let { String.format("%.2f", it) } + "€"
        }

        binding.btReturn.setOnClickListener {
            activity?.finish()
        }

        binding.buttonNext.setOnClickListener {
            val action = TicketInformationFragmentDirections.actionTicketInformationFragmentToParticipantFragment()
            findNavController().navigate(action.actionId, bundleOf("PARTICIPANT" to numberOfTicket))
        }

        //get current event information
        currentEvent =  viewModel.getCurrentEvent()
        Log.d("TicketInformationFragment", "currentEvent: $currentEvent")

        binding.apply {
            imgHeaderTicketInformation.load(currentEvent?.image){
                crossfade(true)
                crossfade(1000)
                placeholder(R.drawable.logo_gradient)
                error(R.drawable.logo_gradient)
            }

            tvTitleTicketInformation.text = "Acheter un ticket pour " + currentEvent?.name
            tvOrganizedBy.text = "Organisé par " + currentEvent?.author
            //round to cents
            tvTotalTicket.text = (currentEvent?.ticketPrice?.times(numberOfTicket)).let { String.format("%.2f", it) } + "€"
        }



    }

}