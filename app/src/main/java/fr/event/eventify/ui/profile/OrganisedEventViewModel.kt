package fr.event.eventify.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.event.GetEventWithAuthorIdUseCase
import fr.event.eventify.ui.home.EventListState
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class OrganisedEventViewModel @Inject constructor(
    private val getEventWithAuthorIdUseCase: GetEventWithAuthorIdUseCase,
    private val getUserUsecase: GetUserUsecase
) : ViewModel() {
    private val _eventList = MutableStateFlow(EventListState())
    val eventList: StateFlow<EventListState> = _eventList

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user

    suspend fun getEventWithAuthorId(authorId: String) {
        _eventList.value = EventListState(isLoading = true)
        try{
            getEventWithAuthorIdUseCase(authorId).collectLatest {
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
            Log.e("MyEventViewModel", "Get event with author id failed", e)
            _eventList.value = EventListState(error = e.message ?: "An unexpected error occurred")
        }
    }

    suspend fun getUser() {
        _user.value = RemoteState(isLoading = true)
        try{
            getUserUsecase().collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _user.value = RemoteState(data = it.data)
                    }
                    is Resource.Error -> {
                        _user.value = RemoteState(error = it.message ?: "An unexpected error occurred")
                    }
                    is Resource.Loading -> {
                        _user.value = RemoteState(isLoading = true)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MyEventViewModel", "Get user failed", e)
            _user.value = RemoteState(error = e.message ?: "An unexpected error occurred")
        }
    }
}