package fr.event.eventify.domain.event

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddParticipantToEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private companion object {
        private const val TAG = "AddParticipantToEventUseCase"
    }
    suspend operator fun invoke(eventId: String, listParticipants :  List<Participant>) : Flow<Resource<Event>> {
        return withContext(ioDispatcher) {
           try {
               eventRepository.addParticipant(eventId, listParticipants)
              } catch (e: Exception) {
                  Log.e(TAG, "Error while adding participant to event: $eventId, error: ${e.message}")
                throw e
           }
        }
    }
}