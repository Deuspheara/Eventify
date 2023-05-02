package fr.event.eventify.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.databinding.FragmentParticipantBinding

class ParticipantFragment : Fragment() {
    private lateinit var binding: FragmentParticipantBinding
    private val viewModel: PaymentViewModel by viewModels()
    private var numberOfParticipant = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipantBinding.inflate(inflater, container, false)
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

        binding.buttonNext.setOnClickListener {
            val action = ParticipantFragmentDirections.actionParticipantFragmentToPaymentSummaryFragment()
            findNavController().navigate(action.actionId, bundleOf("PARTICIPANT" to numberOfParticipant))
        }

        val adapter = ParticipantAdapter()
        binding.rvParticipant.adapter = adapter
        binding.rvParticipant.layoutManager = LinearLayoutManager(requireContext())
        for (i in 1 until numberOfParticipant + 1) {
            adapter.participantList.add(Participant(participantNumber = "Participant $i"))
        }
        adapter.notifyDataSetChanged()
    }

}