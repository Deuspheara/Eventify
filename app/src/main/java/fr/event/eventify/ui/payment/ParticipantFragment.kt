package fr.event.eventify.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import fr.event.eventify.core.models.payment.local.Participant
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentParticipantBinding
import fr.event.eventify.ui.payment.adapter.ParticipantAdapter

@AndroidEntryPoint
class ParticipantFragment : Fragment() {
    private lateinit var binding: FragmentParticipantBinding
    private val viewModel: ParticipantViewModel by viewModels()
    private var numberOfParticipant = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParticipantBinding.inflate(inflater, container, false)
        arguments?.let {
            numberOfParticipant = it.getInt("PARTICIPANT")
        }
        binding.apply {
            imgHeaderTicketInformation.load(viewModel.getCurrentEvent()?.image){
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.logo_gradient)
                error(R.drawable.logo_gradient)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ParticipantAdapter()
        binding.rvParticipant.adapter = adapter
        binding.rvParticipant.layoutManager = LinearLayoutManager(requireContext())
        for (i in 1 until numberOfParticipant + 1) {
            adapter.participantList.add(Participant(participantNumber = "Participant $i"))
        }
        adapter.notifyDataSetChanged()

        binding.buttonPrevious.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonNext.setOnClickListener {

            adapter.participantList.forEach {
                if(!verifyParticipantFields(it)){
                    Toast.makeText(requireContext(), "Veuillez remplir tous les champs correctement", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val action = ParticipantFragmentDirections.actionParticipantFragmentToPaymentSummaryFragment()
            val bundle = bundleOf(
                "PARTICIPANT" to numberOfParticipant,
                "participants" to adapter.participantList,

            )
            findNavController().navigate(action.actionId, bundle)
        }


    }

    fun verifyParticipantFields(participant: Participant) : Boolean{
        if(participant.firstName.isEmpty() || participant.lastName.isEmpty() || participant.participantNumber.isEmpty() || participant.email.isEmpty()){
            return false
        }
        return verifyEmail(participant.email)
    }

    private fun verifyEmail(email: String) : Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}