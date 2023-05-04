package fr.event.eventify.ui.favorite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel: FavoriteViewModel by activityViewModels()
    private var currentUser: RemoteUser? = null
    private lateinit var pagingAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getEventsPaginated(null, FilterEvent.NAME, CategoryEvent.FESTIVAL)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e("FavoriteFragment", "Error while getting user $it")
                    }
                }
                state.isLoading.let {

                }
                state.data?.let { user ->
                    currentUser = user
                    pagingAdapter = FavoriteAdapter(user.uuid) { id, isFavorite ->
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
                    binding.rvFavorite.adapter = pagingAdapter
                    binding.rvFavorite.layoutManager =  LinearLayoutManager(requireContext())
                    getData()
                    Log.d("FavoriteFragment", "User: $user")
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch{
            viewModel.getUser()
        }

    }

    private fun getData() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            try {
                viewModel.eventsPaginated.collectLatest { state ->
                    if (state.error.isNotEmpty()) {
                        state.error.let {
                            Log.e("FavoriteFragment", "Error while collecting events paginated $it")
                        }
                    }
                    state.isLoading.let {
                        Log.d("FavoriteFragment", "isLoading: $it")
                    }
                    state.data?.let {
                        it.map {
                            Log.d("FavoriteFragment", "event: ${it.name}")
                        }
                        pagingAdapter.submitData(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("FavoriteFragment", "Error while collecting events paginated", e)
            }
        }
    }

}