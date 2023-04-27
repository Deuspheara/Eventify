package fr.event.eventify.domain.event

import android.util.Log
import androidx.paging.PagingData
import com.google.rpc.context.AttributeContext.Resource
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.data.repository.event.EventRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetEventsPaginatedUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {

    private companion object {
        private const val TAG = "GetEventsPaginatedUseCase"
    }

    suspend operator fun invoke(
        orderBy: FilterEvent?,
        category : CategoryEvent?
    ) :  Flow<PagingData<Event>> {
        return withContext(ioDispatcher) {
            try {
                eventRepository.getEventPaginated(orderBy, category)
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting events paginated", e)
                throw e
            }
        }
    }
}