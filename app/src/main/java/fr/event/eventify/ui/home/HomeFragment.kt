package fr.event.eventify.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.databinding.FragmentHomeBinding
import fr.event.eventify.ui.profile.ProfileActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var pagingAdapter: EventPagingAdapter
    private var currentUser: RemoteUser? = null
    private var isConnect = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        pagingAdapter = EventPagingAdapter { id, isFavorite -> }
                        binding.rvEvents.adapter = pagingAdapter
                        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
                        getData()
                        Log.e("HomeFragment", "Error while getting user $it")
                    }
                }
                state.isLoading.let {

                }
                state.data?.let { user ->
                    currentUser = user
                    pagingAdapter = EventPagingAdapter(user.uuid) { id, isFavorite ->
                        if (isFavorite) {
                            viewLifecycleOwner.lifecycle.coroutineScope.launch {
                                currentUser?.let { user ->
                                    viewModel.addInterestedUserToEvent(
                                        id,
                                        listOf(user.uuid)
                                    )
                                }
                            }
                        }
                        else {
                            viewLifecycleOwner.lifecycle.coroutineScope.launch {
                                currentUser?.let { user ->
                                    viewModel.deleteInterestedUserToEvent(
                                        id,
                                        listOf(user.uuid)
                                    )
                                }
                            }
                        }
                    }
                    binding.rvEvents.adapter = pagingAdapter
                    binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
                    getData()
                    Log.d("HomeFragment", "User: $user")
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch{
            viewModel.getUser()
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getEventsPaginated(null, FilterEvent.NAME, CategoryEvent.FESTIVAL)
        }

        binding.apply {
            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewLifecycleOwner.lifecycle.coroutineScope.launch {
                        viewModel.getEventsPaginated(
                            query,
                            FilterEvent.NAME,
                            CategoryEvent.FESTIVAL
                        )
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    return false
                }
            })

            search.setOnClickListener(View.OnClickListener {
                search.isIconified = false
            })

            btReturn.setOnClickListener{
                if (currentUser != null) {
                    val intent = Intent(it.context, ProfileActivity::class.java)
                    startActivity(intent)
                }
                else {
                    val action = HomeFragmentDirections.actionHomeFragmentToConnexionFragment()
                    findNavController().navigate(action.actionId)
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.connected.collectLatest { state ->
                state.error.let { message ->
                    //Log.d("HomeFragment", "error: $message")
                    isConnect = false
                }
                state.data?.let {
                    isConnect = it
                }
            }

            viewModel.isConnected()
        }

    }

    private fun getData() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            try {
                viewModel.eventsPaginated.collectLatest { state ->
                    if (state.error.isNotEmpty()) {
                        state.error.let {
                            Log.e("HomeFragment", "Error while collecting events paginated $it")
                        }
                    }
                    state.isLoading.let {
                        Log.d("HomeFragment", "isLoading: $it")
                    }
                    state.data?.let {
                        it.map {
                            Log.d("HomeFragment", "event: ${it.name}")
                        }
                        pagingAdapter.submitData(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error while collecting events paginated", e)
            }
        }
    }


}