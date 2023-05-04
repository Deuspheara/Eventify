package fr.event.eventify.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.domain.event.CreateEventUseCase
import fr.event.eventify.domain.auth.IsConnectedUseCase
import fr.event.eventify.domain.event.GetEventsPaginatedUseCase
import fr.event.eventify.domain.event.GetEventsUseCase
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.logging.Filter
import javax.inject.Inject


data class EventState(
    val data: Event? = null,
    val error: String = "",
    val isLoading: Boolean = false
)
data class EventListState(
    val data: List<Event>? = null,
    val error: String = "",
    val isLoading: Boolean = false
)

data class EventPaginatedState(
    val data:  PagingData<Event>? = null,
    val error: String = "",
    val isLoading: Boolean = false
)

data class ConnectedState(
    val data: Boolean? = null,
    val error: String = "",
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val isConnectedUseCase: IsConnectedUseCase,
    private val getEventsPaginatedUseCase: GetEventsPaginatedUseCase,
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel(){

    private val _event = MutableStateFlow(EventState())
    val event: StateFlow<EventState> = _event

    private val _connected = MutableStateFlow(ConnectedState())
    val connected: StateFlow<ConnectedState> = _connected

    private val _eventPaginated = MutableStateFlow(EventPaginatedState())
    val eventsPaginated: StateFlow<EventPaginatedState> = _eventPaginated

    private val _eventList = MutableStateFlow(EventListState())
    val eventList: StateFlow<EventListState> = _eventList

    fun createEvent(event: Event) {
        try {
            viewModelScope.launch {
                createEventUseCase(event).collect {
                    when(it) {
                        is Resource.Loading -> {
                            _event.value = EventState(isLoading = true)
                        }
                        is Resource.Success -> {
                            _event.value = EventState(data = it.data)
                        }
                        is Resource.Error -> {
                            _event.value = EventState(error = it.message ?: "Unknown error")
                        }
                    }
                }
            }
        }catch (e: Exception) {
            _event.value = EventState(error = e.message ?: "Unknown error")
        }
    }

    fun isConnected() {
        try {
            viewModelScope.launch {
                isConnectedUseCase().collect {
                    when(it) {
                        true -> {
                            _connected.value = ConnectedState(data = true)
                        }
                        false -> {
                            _connected.value = ConnectedState(data = false)
                        }
                    }
                }
            }
        }catch (e: Exception) {
            _connected.value = ConnectedState(error = e.message ?: "Unknown error")
        }
    }

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

    suspend fun getEvents(
        page: Int,
        limit: Int,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ) {
        _eventList.value = EventListState(isLoading = true)
        try {
            viewModelScope.launch {
                getEventsUseCase(page, limit, orderBy, category)
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error while getting events", e)
            _eventList.value = EventListState(error = e.message ?: "Unknown error")
            throw e
        }
    }




}