package fr.event.eventify.domain.event

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetEventWithAuthorIdUseCase @Inject constructor(
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(authorId: String) : Flow<Resource<List<Event>>> {
        return try {
            eventRepository.getEventWithAuthorId(authorId).flowOn(ioDispatcher)
        } catch (e: Exception) {
            Log.e("GetEventWithAuthorIdUseCase", "Get event with author id failed", e)
            throw e
        }
    }
}