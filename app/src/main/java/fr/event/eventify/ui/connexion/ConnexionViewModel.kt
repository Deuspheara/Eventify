package fr.event.eventify.ui.connexion

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.domain.auth.SignInWithEmailUseCase
import fr.event.eventify.domain.auth.SignInWithGoogleUseCase
import fr.event.eventify.ui.register.AuthState
import fr.event.eventify.ui.register.RegisterViewModel
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
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
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

    fun loginWithGoogle(idToken : String) {
        try {

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            viewModelScope.launch(ioDispatcher) {
                signInWithGoogleUseCase(credential).collect {
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
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            _user.value = AuthState(error = "Google sign in failed")
        }
    }
}