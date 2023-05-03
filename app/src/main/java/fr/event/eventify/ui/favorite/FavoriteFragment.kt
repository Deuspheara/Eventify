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
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel: FavoriteViewModel by activityViewModels()

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

        val pagingAdapter = FavoriteAdapter()

        binding.rvFavorite.adapter = pagingAdapter
        binding.rvFavorite.layoutManager =  LinearLayoutManager(requireContext())

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