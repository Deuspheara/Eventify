package fr.event.eventify.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.domain.auth.CreateEventUseCase
import fr.event.eventify.domain.auth.IsConnectedUseCase
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


data class EventState(
    val data: Event? = null,
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
    private val isConnectedUseCase: IsConnectedUseCase
) : ViewModel(){

    private val _event = MutableStateFlow(EventState())
    val event: StateFlow<EventState> = _event

    private val _connected = MutableStateFlow(ConnectedState())
    val connected: StateFlow<ConnectedState> = _connected

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


}