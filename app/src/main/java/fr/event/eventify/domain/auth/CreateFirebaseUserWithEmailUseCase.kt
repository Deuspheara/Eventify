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
    ) : Flow<Resource<FirebaseUser>> {
        return withContext(ioDispatcher) {
            try {
                Log.d(TAG, "Creating new user with email: $email and password $password")
                authRepository.createFirebaseUserWithEmail(email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Error while creating new user with email: $email and password $password, error: ${e.message}")
                throw e
            }
        }
    }
}
