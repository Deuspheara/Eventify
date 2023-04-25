package fr.event.eventify.domain.auth

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.repository.auth.AuthRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private companion object {
        private const val TAG = "SignInWithEmailUseCase"
    }

    suspend operator fun invoke(
        email: String,
        password: String
    ) : Flow<Resource<FirebaseUser>> {
        return withContext(ioDispatcher) {
            try {
                authRepository.signInWithEmail(email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Error while signing in with email: $email and password $password, error: ${e.message}")
                throw e
            }
        }
    }

}