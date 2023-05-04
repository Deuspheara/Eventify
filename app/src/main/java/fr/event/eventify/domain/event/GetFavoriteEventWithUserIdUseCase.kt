package fr.event.eventify.domain.event

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetFavoriteEventWithUserIdUseCase @Inject constructor(
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val eventRepository: EventRepository
) {

    private companion object {
        private const val TAG = "GetFavoriteEventWithUserIdUseCase"
    }

    suspend operator fun invoke(userId: String) : Flow<Resource<List<Event>>> {
        return try {
            eventRepository.getFavoriteEventWithUserId(userId).flowOn(ioDispatcher)
        } catch (e: Exception) {
            Log.e(TAG, "Get event with user id failed", e)
            throw e
        }
    }

}