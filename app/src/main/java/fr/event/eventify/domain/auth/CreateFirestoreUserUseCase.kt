package fr.event.eventify.domain.auth

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.remote.RemoteUser
import fr.event.eventify.data.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateFirestoreUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {

    private companion object {
        private const val TAG = "CreateFirestoreUserUseCase"
    }

    suspend operator fun invoke(
        remoteUser: RemoteUser
    ) {
        return withContext(ioDispatcher) {
            try {
                authRepository.createFirestoreUser(remoteUser)
            } catch (e: Exception) {
                Log.e(TAG, "Error while creating new user in firestore, error: ${e.message}")
                throw e
            }
        }
    }

}