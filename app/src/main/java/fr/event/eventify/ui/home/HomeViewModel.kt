package fr.event.eventify.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.event.CreateEventUseCase
import fr.event.eventify.domain.auth.IsConnectedUseCase
import fr.event.eventify.domain.event.AddInterestedUserToEventUseCase
import fr.event.eventify.domain.event.DeleteInterestedUserToEventUseCase
import fr.event.eventify.domain.event.GetEventsPaginatedUseCase
import fr.event.eventify.domain.event.GetEventsUseCase
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private val addInterestedUserToEventUseCase: AddInterestedUserToEventUseCase,
    private val deleteInterestedUserToEventUseCase: DeleteInterestedUserToEventUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val isConnectedUseCase: IsConnectedUseCase,
    private val getEventsPaginatedUseCase: GetEventsPaginatedUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    private val getUserUseCase: GetUserUsecase
) : ViewModel(){

    private val _event = MutableStateFlow(EventState())
    val event: StateFlow<EventState> = _event

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user

    private val _connected = MutableStateFlow(ConnectedState())
    val connected: StateFlow<ConnectedState> = _connected

    private val _eventPaginated = MutableStateFlow(EventPaginatedState())
    val eventsPaginated: StateFlow<EventPaginatedState> = _eventPaginated

    //TODO: Remove this test
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

    suspend fun addInterestedUserToEvent(eventId: String, interestedUsers: List<String>) {
        viewModelScope.launch {
            addInterestedUserToEventUseCase(eventId, interestedUsers).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _event.value = EventState(data = it.data)
                    }
                    is Resource.Error -> {
                        _event.value = EventState(error = it.message ?: "An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _event.value = EventState(isLoading = true)
                    }
                }

            }
        }
    }

    suspend fun deleteInterestedUserToEvent(eventId: String, interestedUsers: List<String>) {
        viewModelScope.launch {
            deleteInterestedUserToEventUseCase(eventId, interestedUsers).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _event.value = EventState(data = it.data)
                    }
                    is Resource.Error -> {
                        _event.value = EventState(error = it.message ?: "An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _event.value = EventState(isLoading = true)
                    }
                }

            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            getUserUseCase().collectLatest {
                when(it){
                    is Resource.Success -> {
                        _user.value = RemoteState(data = it.data)
                    }
                    is Resource.Error -> {
                        _user.value = RemoteState(error = it.message ?: "Error while getting user")
                    }
                    else -> {
                        _user.value = RemoteState(error = "Error")
                    }
                }
            }
        }
    }


}