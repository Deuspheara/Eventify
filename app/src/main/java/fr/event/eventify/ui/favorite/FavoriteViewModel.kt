package fr.event.eventify.ui.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.event.AddInterestedUserToEventUseCase
import fr.event.eventify.domain.event.DeleteInterestedUserToEventUseCase
import fr.event.eventify.domain.event.GetFavoriteEventWithUserIdUseCase
import fr.event.eventify.ui.home.EventListState
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
    private val getFavoriteEventWithUserIdUseCase: GetFavoriteEventWithUserIdUseCase,
    private val getUserUseCase: GetUserUsecase
) : ViewModel() {

    private val _eventList = MutableStateFlow(EventListState())
    val eventList: StateFlow<EventListState> = _eventList

    private val _event = MutableStateFlow(EventState())
    val event: StateFlow<EventState> = _event

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user

    suspend fun getFavoriteEventWithUserId(userId: String) {
        _eventList.value = EventListState(isLoading = true)
        try{
            getFavoriteEventWithUserIdUseCase(userId).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _eventList.value = EventListState(data = it.data)
                    }
                    is Resource.Error -> {
                        _eventList.value = EventListState(error = it.message ?: "An unexpected error occurred")
                    }
                    is Resource.Loading -> {
                        _eventList.value = EventListState(isLoading = true)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FavoriteViewModel", "Get event with user id failed", e)
            _eventList.value = EventListState(error = e.message ?: "An unexpected error occurred")
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