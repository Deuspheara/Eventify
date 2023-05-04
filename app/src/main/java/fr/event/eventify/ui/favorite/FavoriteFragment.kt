package fr.event.eventify.ui.favorite

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
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel: FavoriteViewModel by activityViewModels()
    private var currentUser: RemoteUser? = null
    private var adapter: FavoriteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e("FavoriteFragment", "Error while collecting user $it")
                    }
                }
                state.data?.let {
                    currentUser = it
                    viewModel.getFavoriteEventWithUserId(it.uuid)
                    adapter = FavoriteAdapter(it.uuid) { id, isFavorite ->
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
                    binding.rvFavorite.adapter = adapter
                    binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
                    getData()
                }
                state.isLoading.let {

                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch{
            viewModel.getUser()
        }

    }

    private fun getData() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.eventList.collectLatest {state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e("MyEventFragment", "Error while collecting events by author uuid $it")
                    }
                }
                state.data?.let {
                    Log.d("MyEventFragment", "data: $it")
                    adapter?.submitList(it)

                }
                state.isLoading.let {

                }
            }
        }
    }

}