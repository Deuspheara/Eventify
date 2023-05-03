package fr.event.eventify.ui.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.domain.auth.IsConnectedUseCase
import fr.event.eventify.domain.event.GetEventsPaginatedUseCase
import fr.event.eventify.domain.event.GetEventsUseCase
import fr.event.eventify.ui.home.EventPaginatedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val isConnectedUseCase: IsConnectedUseCase,
    private val getEventsPaginatedUseCase: GetEventsPaginatedUseCase,
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {

    private val _eventPaginated = MutableStateFlow(EventPaginatedState())
    val eventsPaginated: StateFlow<EventPaginatedState> = _eventPaginated

    suspend fun getEventsPaginated(
        name: String?,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ) {
        _eventPaginated.value = EventPaginatedState(isLoading = true)
        try {
            viewModelScope.launch {
                getEventsPaginatedUseCase(name, orderBy, category)
                    .cachedIn(viewModelScope)
                    .collect {
                        Log.d("HomeViewModel", "Got events: $it")
                        it.map {
                            Log.d("HomeViewModel", "Got event: $it")
                        }
                        _eventPaginated.value = EventPaginatedState(
                            data = it,
                        )
                    }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error while getting events paginated", e)
            _eventPaginated.value = EventPaginatedState(error = e.message ?: "Unknown error")
        }
    }
}