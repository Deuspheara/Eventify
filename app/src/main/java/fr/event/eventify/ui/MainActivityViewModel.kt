package fr.event.eventify.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.domain.auth.IsConnectedUseCase
import fr.event.eventify.ui.home.ConnectedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val isConnectedUseCase: IsConnectedUseCase
) : ViewModel() {

    private val _connected = MutableStateFlow(ConnectedState())
    val connected: StateFlow<ConnectedState> = _connected

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