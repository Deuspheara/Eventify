package fr.event.eventify.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.auth.LogoutUseCase
import fr.event.eventify.domain.event.GetEventWithAuthorIdUseCase
import fr.event.eventify.ui.home.ConnectedState
import fr.event.eventify.ui.home.EventListState
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUsecase: GetUserUsecase,
    private val getEventWithAuthorIdUseCase: GetEventWithAuthorIdUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user

    private val _eventList = MutableStateFlow(EventListState())
    val eventList: StateFlow<EventListState> = _eventList

    private val _logout = MutableStateFlow(ConnectedState())
    val logout: MutableStateFlow<ConnectedState> = _logout

    fun getUser() {
        try {
            viewModelScope.launch {
                getUserUsecase().collect {
                    when (it) {
                        is Resource.Loading -> {
                            _user.value = RemoteState(isLoading = true)
                        }
                        is Resource.Success -> {
                            _user.value = RemoteState(data = it.data)
                        }
                        is Resource.Error -> {
                            _user.value = RemoteState(error = it.message ?: "Unknown error")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while getting user", e)
            throw e
        }
    }

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

    fun logout() {
        try {
            viewModelScope.launch {
                logoutUseCase().collect {
                    if(it){
                        _logout.value = ConnectedState(data = false)
                    } else {
                        _logout.value = ConnectedState(error = "An unexpected error occurred")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while logging out", e)
            throw e
        }
    }


}