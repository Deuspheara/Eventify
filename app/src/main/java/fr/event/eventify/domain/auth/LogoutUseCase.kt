package fr.event.eventify.domain.auth

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.repository.auth.AuthRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) {

    private companion object {
        private const val TAG = "LogoutUseCase"
    }

    suspend operator fun invoke() : Flow<Boolean> {
        return withContext(dispatcher) {
            try {
                authRepository.logout()
            } catch (e: Exception) {
                Log.e(TAG, "Error while logging out, error: ${e.message}")
                throw e
            }
        }
    }
}