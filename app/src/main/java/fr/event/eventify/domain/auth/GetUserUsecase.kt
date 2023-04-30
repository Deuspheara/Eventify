package fr.event.eventify.domain.auth

import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.data.repository.auth.AuthRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUserUsecase @Inject constructor(
    private val authRepository: AuthRepository,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() : Flow<Resource<RemoteUser>> {
        return try {
            authRepository.getUser().flowOn(dispatcher)
        } catch (e: Exception) {
            throw e
        }
    }
}