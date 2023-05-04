package fr.event.eventify.ui.profile.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.core.mapper.EventMapper
import fr.event.eventify.databinding.FragmentParticipateEventBinding
import fr.event.eventify.ui.profile.ParticipateEventViewModel
import fr.event.eventify.ui.profile.adapter.ParticipateEventAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ParticipateEventFragment : Fragment() {
   private lateinit var binding: FragmentParticipateEventBinding
   private lateinit var adapter: ParticipateEventAdapter
   private val viewModel: ParticipateEventViewModel by viewModels()

    private companion object {
        private const val TAG = "ParticipateEventFragment"
    }

    override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
    ): View {
         binding = FragmentParticipateEventBinding.inflate(inflater, container, false)
            adapter = ParticipateEventAdapter()

        binding.rvParticipateEvent.adapter = adapter
        binding.rvParticipateEvent.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.eventList.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e(TAG, "Error while collecting joined events $it")
                    }
                }
                state.isLoading.let {
                    Log.d(TAG, "Loading joined events")
                }
                state.data?.let {
                    adapter.submitList(EventMapper.mapEventListToEventLightList(it))
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getJoinedEvents()
        }
    }
}