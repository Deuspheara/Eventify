package fr.event.eventify.ui.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.event.GetCurrentEventUseCase
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ParticipantViewModel @Inject constructor(
    private val getCurrentEventUseCase: GetCurrentEventUseCase,
    private val getUserUsecase: GetUserUsecase
) : ViewModel() {

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user

    fun getCurrentEvent(): EventLight? {
        return getCurrentEventUseCase()
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