package fr.event.eventify.data.datasource.event.remote

import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.PagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.OrderBy
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.core.models.event.remote.stringValue
import fr.event.eventify.data.paging.EventPagingSource
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface EventRemoteDataSource {
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
    suspend fun getEvents(page: Int, limit: Int, orderBy: FilterEvent?, category: CategoryEvent?): Resource<List<Event>>

    fun createCharacterPagingSource(orderBy: FilterEvent?, category: CategoryEvent?): PagingSource<Int, Event>
}

class EventRemoteDataSourceImpl @Inject constructor(
    @DispatcherModule.DispatcherIO private val ioContext: CoroutineDispatcher,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : EventRemoteDataSource{

    private companion object {
        private const val TAG = "EventRemoteDataSource"
    }

    override suspend fun createEvent(event: Event): Flow<Resource<Event>> = flow {
        emit(Resource.Loading())
        Log.d(TAG, "Creating event $event")
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                emit(Resource.Error(message = "User not connected"))
            }else {
                val eventRef = firebaseFirestore.collection("Events").document()
                val eventWithId = event.copy(id = eventRef.id)
                eventRef.set(eventWithId).await()
                emit(Resource.Success(eventWithId))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while creating event", e)
            emit(Resource.Error(
                message = e.message ?: "Error while creating event",
            ))
            throw e
        }
    }.flowOn(ioContext)

    override suspend fun getEvents(
        page: Int,
        limit: Int,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): Resource<List<Event>> {
        try {
            Log.d(TAG, "Getting events page $page with limit $limit")
            val user = firebaseAuth.currentUser
            if (user == null) {
                return Resource.Error(message = "User not connected")
            } else {
                val events = firebaseFirestore.collection("Events")
                .orderBy(orderBy?.stringValue ?: FilterEvent.NAME.stringValue)
                .startAfter(page.toLong() * limit)
                .limit(limit.toLong())
                    .get()
                    .await()
                    .toObjects(Event::class.java)

                return if (events.isNotEmpty()) {
                    Resource.Success(events)
                } else {
                    Resource.Success(emptyList())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while getting events", e)
            return Resource.Error(message = e.message ?: "Error while getting events")
        }
    }



    override fun createCharacterPagingSource(
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): PagingSource<Int, Event> {
        return EventPagingSource(
            getEvents = { page, limit, ->
                getEvents(page, limit, orderBy, category)
            },
            dispatcher = ioContext
        )
    }

}