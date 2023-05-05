package fr.event.eventify.ui.profile_detail.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentOrganisedEventDetailBinding
import fr.event.eventify.ui.profile.fragment.ParticipateEventFragment
import fr.event.eventify.ui.profile_detail.adapter.OrganisedEventDetailAdapter
import fr.event.eventify.ui.profile_detail.viewmodel.OrganisedEventDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrganisedEventDetailFragment : Fragment() {
   private lateinit var binding: FragmentOrganisedEventDetailBinding
   private lateinit var adapter: OrganisedEventDetailAdapter
   private val viewModel: OrganisedEventDetailViewModel by viewModels()

    private companion object {
        private const val TAG = "OrganisedEventDetailFragment"
    }

    override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
    ): View {
          binding = FragmentOrganisedEventDetailBinding.inflate(inflater, container, false)
            adapter = OrganisedEventDetailAdapter()
            binding.apply {
                rvDetailOrganisedEvent.adapter = adapter
                rvDetailOrganisedEvent.layoutManager = LinearLayoutManager(requireContext())
                tvOrganisedEventName.text = arguments?.getString("eventName")
                imgOrganisedEventDetail.load(arguments?.getString("eventImage")){
                    placeholder(R.drawable.logo_gradient)
                    error(R.drawable.logo_gradient)
                }
                btBackOrganisedEventDetail.setOnClickListener {
                    findNavController().popBackStack()
                }
            }

          return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            arguments?.getString("eventId")?.let {
                viewModel.getTransactionsByEventId(it)
            }
        }
    }

    fun setupObservers() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.transactionList.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e(TAG, "Error while collecting joined events $it")
                    }
                }
                state.isLoading.let {
                }
                state.transactions.let {
                    adapter.submitList(it)
                }
            }
        }
    }
}