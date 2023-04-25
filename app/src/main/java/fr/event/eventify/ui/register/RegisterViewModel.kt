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
import fr.event.eventify.core.models.remote.RemoteUser
import fr.event.eventify.domain.auth.CreateFirebaseUserWithEmailUseCase
import fr.event.eventify.domain.auth.CreateFirestoreUserUseCase
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

data class RemoteState(
    val data: RemoteUser? = null,
    val error: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val createFirebaseUserWithEmailUseCase: CreateFirebaseUserWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val createFirestoreUserUseCase: CreateFirestoreUserUseCase,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _user = MutableStateFlow(AuthState())
    val user: StateFlow<AuthState> = _user

    private val _remoteUser = MutableStateFlow(RemoteState())
    val remoteUser: StateFlow<RemoteState> = _remoteUser


    /**
     * Register a new user
     * @param email the email of the user
     * @param password the password of the user
     */
    fun register(email: String, password: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
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
            } catch (e: Exception) {
                Log.e(TAG, "Error while creating new user, error: ${e.message}")
                _user.value = AuthState(error = "Error while creating new user")
            }

        }
    }


    /**
     * Sign in with google
     * @param idToken the idToken of the google account
     */
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

    fun registerOnFireStore(remoteUser: RemoteUser) {
        Log.d(TAG, "registerOnFireStore: $remoteUser")
        viewModelScope.launch(ioDispatcher) {
            try {
                createFirestoreUserUseCase(remoteUser).collect {
                    when(it) {
                        is Resource.Loading -> {
                            _remoteUser.value = RemoteState(isLoading = true)
                        }
                        is Resource.Success -> {
                            _remoteUser.value = RemoteState(data = it.data)
                        }
                        is Resource.Error -> {
                            _remoteUser.value = RemoteState(error = it.message ?: "Unknown error")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register on firestore failed", e)
                _remoteUser.value = RemoteState(error = "Register on firestore failed")
            }

        }
    }


}
