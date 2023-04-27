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

class CreateEventUseCase  @Inject constructor(
    private val eventRepository: EventRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
   private companion object {
        private const val TAG = "CreateEventUseCase"
   }

    suspend operator fun invoke(
        event: Event
    ) : Flow<Resource<Event>> {
        return withContext(ioDispatcher) {
            try {
                Log.d(TAG, "Creating new event: $event")
                eventRepository.createEvent(event)
            } catch (e: Exception) {
                Log.e(TAG, "Error while creating new event: $event, error: ${e.message}")
                throw e
            }
        }
    }
}