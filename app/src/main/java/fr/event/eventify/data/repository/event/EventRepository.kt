package fr.event.eventify.data.repository.event

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.core.models.payment.local.Participant
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

    fun getCurrentEvent(): EventLight?

    fun setCurrentEvent(event: EventLight)

    suspend fun addParticipant(eventId: String, listParticipants :  List<Participant>): Flow<Resource<Event>>

    /**
     * Get all events of an author
     * @param authorId the id of the author
     * @return a [Flow] of [Resource] of [List] of [Event]
     * @see Event
     */
    suspend fun getEventWithAuthorId(authorId: String): Flow<Resource<List<Event>>>

    /**
     * Get all events of an author
     * @param authorId the id of the author
     * @return a [Flow] of [Resource] of [List] of [Event]
     * @see Event
     */
    suspend fun getJoinedEvents(): Flow<Resource<List<Event>>>

    /**
     * Get all events of an author
     * @param authorId the id of the author
     * @return a [Flow] of [Resource] of [List] of [Event]
     * @see Event
     */
    suspend fun getJoinedEventsRaw(): Flow<Resource<List<RemoteUser.JoinedEvent>>>

    suspend fun addInterestedUser(eventId: String, interestedUsers : List<String>): Flow<Resource<Event>>

    suspend fun deleteInterestedUser(eventId: String, interestedUsers : List<String>): Flow<Resource<Event>>

    suspend fun getFavoriteEventWithUserId(userId: String): Flow<Resource<List<Event>>>
}

class EventRepositoryImpl @Inject constructor(
    private val EventRemoteDataSource: EventRemoteDataSource,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : EventRepository {

    private companion object {
        private const val TAG = "EventRepository"
        private var currentEvent: EventLight? = null
    }

    /**
     * Set the current event
     * @param event the [EventLight] to set
     */
    override fun setCurrentEvent(event: EventLight){
        currentEvent = event
    }

    override suspend fun addParticipant(
        eventId: String,
        listParticipants: List<Participant>
    ): Flow<Resource<Event>> {
        return withContext(ioDispatcher) {
            try {
                EventRemoteDataSource.addParticipant(eventId, listParticipants)
            } catch (e: Exception) {
                Log.e(TAG, "Error while adding participant with $eventId", e)
                throw e
            }
        }
    }

    override suspend fun addInterestedUser(
        eventId: String,
        interestedUsers: List<String>
    ): Flow<Resource<Event>> {
        return withContext(ioDispatcher) {
            try {
                EventRemoteDataSource.addInterestedUser(eventId, interestedUsers)
            } catch (e: Exception) {
                Log.e(TAG, "Error while adding interested user with $eventId", e)
                throw e
            }
        }
    }

    override suspend fun deleteInterestedUser(
        eventId: String,
        interestedUsers: List<String>
    ): Flow<Resource<Event>> {
        return withContext(ioDispatcher) {
            try {
                EventRemoteDataSource.deleteInterestedUser(eventId, interestedUsers)
            } catch (e: Exception) {
                Log.e(TAG, "Error while deleting interested user with $eventId", e)
                throw e
            }
        }
    }

    override suspend fun getFavoriteEventWithUserId(userId: String): Flow<Resource<List<Event>>> {
        return withContext(ioDispatcher) {
            try {
                EventRemoteDataSource.getFavoriteEventWithUserId(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting event with $userId", e)
                throw e
            }
        }
    }

    override suspend fun getEventWithAuthorId(authorId: String): Flow<Resource<List<Event>>> {
        return withContext(ioDispatcher) {
            try {
                EventRemoteDataSource.getEventWithAuthorId(authorId)
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting event with $authorId", e)
                throw e
            }
        }
    }

    override suspend fun getJoinedEvents(): Flow<Resource<List<Event>>> {
        return withContext(ioDispatcher) {
            try {
                EventRemoteDataSource.getJoinedEvents()
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting joined events", e)
                throw e
            }
        }
    }

    override suspend fun getJoinedEventsRaw(): Flow<Resource<List<RemoteUser.JoinedEvent>>> {
        return withContext(ioDispatcher) {
            try {
                EventRemoteDataSource.getJoinedEventsRaw()
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting joined events", e)
                throw e
            }
        }
    }

    /**
     * Get the current event
     * @return the current [EventLight]
     */
    override fun getCurrentEvent(): EventLight?{
        return currentEvent
    }

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