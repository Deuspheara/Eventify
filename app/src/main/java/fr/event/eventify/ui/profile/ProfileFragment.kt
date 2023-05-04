package fr.event.eventify.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentProfileBinding
import fr.event.eventify.ui.register.RemoteState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
   private lateinit var binding: FragmentProfileBinding
   private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
    ): View {
         binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.btReturnProfile.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest {state ->
                state.isLoading?.let {

                }
                state.data?.let {
                    binding.apply {
                        tvProfileName.text = it.pseudo
                        imgProfile.load(it.photoUrl){
                            placeholder(R.drawable.pingouin)
                            error(R.drawable.pingouin)
                        }
                        tvNbOrganise.text = it.createdEvents.size.toString()
                        tvNbParticipate.text = it.joinedEvents.size.toString()

                    }
                }
                state.error.let {
                    Log.e("ProfileFragment", "Error while collecting user $it")
                }
            }
        }

        binding.btnModifyProfile.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToModifyProfileFragment()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getUser()
        }
    }
}