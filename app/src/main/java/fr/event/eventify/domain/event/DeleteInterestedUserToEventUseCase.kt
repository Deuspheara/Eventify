package fr.event.eventify.domain.event

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteInterestedUserToEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private companion object {
        private const val TAG = "DeleteInterestedUserToEventUseCase"
    }
    suspend operator fun invoke(eventId: String, interestedUsers: List<String>) : Flow<Resource<Event>> {
        return withContext(ioDispatcher) {
            try {
                eventRepository.deleteInterestedUser(eventId, interestedUsers)
            } catch (e: Exception) {
                Log.e(TAG, "Error while deleting interested user to event: $eventId, error: ${e.message}")
                throw e
            }
        }
    }
}