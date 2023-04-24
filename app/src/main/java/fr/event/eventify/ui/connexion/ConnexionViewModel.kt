package fr.event.eventify.ui.connexion

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.domain.auth.SignInWithEmailUseCase
import fr.event.eventify.ui.register.AuthState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnexionViewModel @Inject constructor(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private companion object {
        private const val TAG = "ConnexionViewModel"
    }

    private val _user = MutableStateFlow(AuthState())
    val user: StateFlow<AuthState> = _user

    fun signIn(email: String, password: String) {
        viewModelScope.launch(ioDispatcher) {
            signInWithEmailUseCase(email, password).collect{
                when(it) {
                    is Resource.Loading -> {
                        _user.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _user.value = AuthState(data = it.data)
                    }
                    is Resource.Error -> {
                        _user.value = AuthState(error = it.message ?: "Unknown error")
                    }
                }
            }
        }
    }
}