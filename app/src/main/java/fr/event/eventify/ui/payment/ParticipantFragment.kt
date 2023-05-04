package fr.event.eventify.ui.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import fr.event.eventify.core.models.payment.local.Participant
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.databinding.FragmentParticipantBinding
import fr.event.eventify.ui.payment.adapter.ParticipantAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ParticipantFragment : Fragment() {
    private lateinit var binding: FragmentParticipantBinding
    private val viewModel: ParticipantViewModel by viewModels()
    private var numberOfParticipant = 1
    private lateinit var adapter: ParticipantAdapter
    private var currentUser : RemoteUser? = null

    private companion object {
        private const val TAG = "ParticipantFragment"
    }

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
        adapter = ParticipantAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e(TAG, "user not found")
                    }
                }
                state.data?.let {
                    currentUser = it
                    binding.rvParticipant.adapter = adapter
                    binding.rvParticipant.layoutManager = LinearLayoutManager(requireContext())
                    for (i in 1 until numberOfParticipant + 1) {
                        adapter.participantList.add(Participant())
                    }

                    adapter.updateCurrentUser(
                        Participant(
                            firstName = "",
                            lastName = "",
                            email = it.email,
                        )
                    )
                }
                state.isLoading.let {
                    Log.d(TAG, "isLoading: $it")
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getUser()
        }

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

    private fun verifyParticipantFields(participant: Participant) : Boolean{
        if(participant.firstName.isEmpty() || participant.lastName.isEmpty() || participant.email.isEmpty()){
            return false
        }
        return verifyEmail(participant.email)
    }

    private fun verifyEmail(email: String) : Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}