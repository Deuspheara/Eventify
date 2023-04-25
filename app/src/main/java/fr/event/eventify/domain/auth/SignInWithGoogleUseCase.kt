package fr.event.eventify.domain.auth

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.repository.auth.AuthRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(

    private val repository: AuthRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private companion object {
        private const val TAG = "SignInWithGoogleUseCase"
    }

    suspend operator fun invoke(
        credential: AuthCredential
    ): Flow<Resource<FirebaseUser>> {
        return withContext(ioDispatcher) {
            try {
                repository.signInWithGoogle(credential)
            } catch (e: Exception) {
               Log.e(TAG, "Error while signing in with google: $credential, error: ${e.message}")
                throw e
            }
        }
    }

}