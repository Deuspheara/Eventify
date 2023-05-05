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
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.databinding.FragmentProfileBinding
import fr.event.eventify.ui.home.ConnectedState
import fr.event.eventify.ui.profile.adapter.ProfilePagerAdapter
import fr.event.eventify.ui.register.RemoteState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
   private lateinit var binding: FragmentProfileBinding
   private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var profilePagerAdapter: ProfilePagerAdapter
    private var currentUser : RemoteUser? = null

    override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
    ): View {
         binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.btReturnProfile.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Initialize the ProfilePagerAdapter
        profilePagerAdapter = ProfilePagerAdapter(childFragmentManager, lifecycle)

        // Set the adapter to the ViewPager
        binding.pager.adapter = profilePagerAdapter

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = profilePagerAdapter.getTabTitle(position)
        }.attach()

        binding.btLogoutProfile.setOnClickListener{
            viewLifecycleOwner.lifecycle.coroutineScope.launch {
                viewModel.logout()
            }
        }

         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest {state ->
                state.isLoading.let {

                }
                state.data?.let {

                    viewModel.getEventWithAuthorId(it.uuid)

                    binding.apply {
                        tvProfileName.text = it.pseudo
                        imgProfile.load(it.photoUrl){
                            placeholder(R.drawable.pingouin)
                            error(R.drawable.pingouin)
                        }
                        tvNbParticipate.text = it.joinedEvents.size.toString()

                    }
                }
                state.error.let {
                    Log.e("ProfileFragment", "Error while collecting user $it")
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.eventList.collectLatest {state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e("MyEventFragment", "Error while collecting events by author uuid $it")
                    }
                }
                state.data?.let {
                    Log.d("MyEventFragment", "nbOrganised: ${it.size}")
                    binding.tvNbOrganise.text = "${it.size}"
                }

            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.logout.collectLatest {state ->
                state.data?.let {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                state.error.let {
                    Log.e("ProfileFragment", "Error while logout $it")

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