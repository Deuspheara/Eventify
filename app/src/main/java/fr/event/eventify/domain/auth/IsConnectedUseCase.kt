package fr.event.eventify.domain.auth

import android.util.Log
import com.google.rpc.context.AttributeContext.Resource
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IsConnectedUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private companion object {
        private const val TAG = "IsConnectedUseCase"
    }

    suspend operator fun invoke() : Flow<Boolean> {
        return withContext(ioDispatcher) {
            try {
                authRepository.isUserConnected()
            } catch (e: Exception) {
                Log.e(TAG, "Error while checking if user is connected", e)
                throw e
            }
        }
    }
}