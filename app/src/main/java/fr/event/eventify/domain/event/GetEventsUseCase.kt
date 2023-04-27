package fr.event.eventify.domain.event

import android.util.Log
import com.google.rpc.context.AttributeContext
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
){
    private companion object {
        private const val TAG = "GetEventsPaginatedRawUseCase"
    }

    suspend operator fun invoke(
        page: Int,
        limit: Int,
        orderBy: FilterEvent?,
        category: CategoryEvent?): Resource<List<Event>> {
        return withContext(ioDispatcher) {
            try {
                eventRepository.getEvents(
                    page,
                    limit,
                    orderBy,
                    category
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting events paginated", e)
                throw e
            }
        }
    }
}