package fr.event.eventify.ui.profile.modify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.auth.ModifyUserUseCase
import fr.event.eventify.ui.profile.ProfileViewModel
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyProfileViewModel @Inject constructor(
    private val getUserUsecase: GetUserUsecase,
    private val modifyUserUseCase: ModifyUserUseCase
) : ViewModel() {

    private companion object {
        private const val TAG = "ModifyProfileViewModel"
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

    fun modifyUser(uuid: String, pseudo: String, description: String, photoUrl: String) {
        try {
            viewModelScope.launch {
                modifyUserUseCase(uuid,pseudo,description,photoUrl).collect {
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