package fr.event.eventify.ui.profile.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.core.mapper.EventMapper
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.databinding.FragmentMyEventBinding
import fr.event.eventify.ui.profile.OrganisedEventViewModel
import fr.event.eventify.ui.profile.adapter.MyEventAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrganisedEventFragment : Fragment() {
    private lateinit var binding: FragmentMyEventBinding
    private val viewModel : OrganisedEventViewModel by activityViewModels()
    private lateinit var adapter: MyEventAdapter
    private var currentUser : RemoteUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyEventBinding.inflate(inflater, container, false)

        adapter = MyEventAdapter()

        binding.rvOrganisedEvent.adapter = adapter


        binding.rvOrganisedEvent.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.eventList.collectLatest {state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e("MyEventFragment", "Error while collecting events by author uuid $it")
                    }
                }
                state.data?.let {
                    Log.d("MyEventFragment", "data: $it")
                    adapter.submitList(
                        EventMapper.mapEventListToEventLightList(it)
                    )

                }
                state.isLoading.let {

                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
           viewModel.user.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                     state.error.let {
                          Log.e("MyEventFragment", "Error while collecting user $it")
                     }
                }
                state.data?.let {
                     currentUser = it

                    viewModel.getEventWithAuthorId(it.uuid)

                }
                state.isLoading.let {

                }
           }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
           viewModel.getUser()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = OrganisedEventFragment()
    }
}