package fr.event.eventify.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUsecase: GetUserUsecase
) : ViewModel() {

    private companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user

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



}