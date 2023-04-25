package fr.event.eventify.ui.register

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.R
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.domain.auth.CreateFirebaseUserWithEmailUseCase
import fr.event.eventify.domain.auth.SignInWithGoogleUseCase
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val data: FirebaseUser? = null,
    val error: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val createFirebaseUserWithEmailUseCase: CreateFirebaseUserWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _user = MutableStateFlow(AuthState())
    val user: StateFlow<AuthState> = _user


    fun register(email: String, password: String) {
        viewModelScope.launch(ioDispatcher) {
            createFirebaseUserWithEmailUseCase(email, password).collect {
                when (it) {
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
