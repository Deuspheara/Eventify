package fr.event.eventify.ui.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.event.AddInterestedUserToEventUseCase
import fr.event.eventify.domain.event.DeleteInterestedUserToEventUseCase
import fr.event.eventify.domain.event.GetEventsPaginatedUseCase
import fr.event.eventify.ui.home.EventPaginatedState
import fr.event.eventify.ui.home.EventState
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val addInterestedUserToEventUseCase: AddInterestedUserToEventUseCase,
    private val deleteInterestedUserToEventUseCase: DeleteInterestedUserToEventUseCase,
    private val getEventsPaginatedUseCase: GetEventsPaginatedUseCase,
    private val getUserUseCase: GetUserUsecase
) : ViewModel() {

    private val _eventPaginated = MutableStateFlow(EventPaginatedState())
    val eventsPaginated: StateFlow<EventPaginatedState> = _eventPaginated

    private val _event = MutableStateFlow(EventState())
    val event: StateFlow<EventState> = _event

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user

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
                        Log.d("FavoriteViewModel", "Got events: $it")
                        it.map {
                            Log.d("FavoriteViewModel", "Got event: $it")
                        }
                        _eventPaginated.value = EventPaginatedState(
                            data = it,
                        )
                    }
            }
        } catch (e: Exception) {
            Log.e("FavoriteViewModel", "Error while getting events paginated", e)
            _eventPaginated.value = EventPaginatedState(error = e.message ?: "Unknown error")
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