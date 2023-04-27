package fr.event.eventify.data.repository.event

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.DocumentSnapshot
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.data.datasource.event.remote.EventRemoteDataSource
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


interface EventRepository {

    /**
     * Create a new event
     * @param event the [Event] to create
     * @return a [Flow] of [Resource]
     */
    suspend fun createEvent(event: Event): Flow<Resource<Event>>

    /**
     * Get all events paginated
     * @param page the page to get
     * @param limit the limit of events to get
     * @return a [Flow] of [Resource]
     */
    suspend fun getEventPaginated(
        name: String?,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): Flow<PagingData<Event>>

    /**
     * Get all events
     * @param page the page to get
     * @param limit the limit of events to get
     * @return a [Flow] of [Resource]
     * @see [EventRemoteDataSource.getEvents]
     */
    suspend fun getEvents(page: Int, limit: Int,  orderBy: FilterEvent?, category: CategoryEvent?): Resource<List<Event>>
}

class EventRepositoryImpl @Inject constructor(
    private val EventRemoteDataSource: EventRemoteDataSource,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : EventRepository {

    private companion object {
        private const val TAG = "EventRepository"
    }

    /**
     * Create a new event
     * @param event the [Event] to create
     * @return a [Flow] of [Resource]
     * @see [EventRemoteDataSource.createEvent]
     */
    override suspend fun createEvent(event: Event): Flow<Resource<Event>> {
       return withContext(ioDispatcher) {
           try {
               EventRemoteDataSource.createEvent(event)
           } catch (e: Exception) {
               Log.e(TAG, "Error while creating event with $event", e)
               throw e
           }
       }
    }

    /**
     * Get all events paginated
     * @param page the page to get
     * @param limit the limit of events to get
     * @return a [Flow] of [Resource]
     * @see [EventRemoteDataSource.getEvents]
     */
    override suspend fun getEventPaginated(
        name: String?,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): Flow<PagingData<Event>> {
        return withContext(ioDispatcher) {
            try {
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        EventRemoteDataSource.createCharacterPagingSource(
                            name,
                            orderBy,
                            category
                        )
                    }
                ).flow
            } catch (e: Exception) {
                Log.e(TAG, "Error while creating event with $orderBy and $category", e)
                throw e
            }
        }
    }

    override suspend fun getEvents(
        page: Int,
        limit: Int,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): Resource<List<Event>> {
        return withContext(ioDispatcher) {
            try {
                EventRemoteDataSource.getEvents(page, limit, orderBy, category)
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting events with $orderBy and $category", e)
                throw e
            }
        }
    }


}