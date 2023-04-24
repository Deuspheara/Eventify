package fr.event.eventify.domain.auth

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateFirebaseUserWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private companion object {
        private const val TAG = "CreateFirebaseUserWithEmailUseCase"
    }

    suspend operator fun invoke(
        email: String,
        password: String
    ) {
        return withContext(ioDispatcher) {
            try {
                authRepository.createFirebaseUserWithEmail(email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Error while creating new user with email: $email and password $password, error: ${e.message}")
                throw e
            }
        }
    }
}
