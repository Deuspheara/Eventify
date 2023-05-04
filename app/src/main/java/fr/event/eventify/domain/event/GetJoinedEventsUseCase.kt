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

class GetJoinedEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) {

    private companion object {
        private const val TAG = "GetJoinedEventsUseCase"
    }

    suspend operator fun invoke() : Flow<Resource<List<Event>>> {
        return withContext(dispatcher) {
            try {
                eventRepository.getJoinedEvents()
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting joined events", e)
                throw e
            }
        }
    }

}