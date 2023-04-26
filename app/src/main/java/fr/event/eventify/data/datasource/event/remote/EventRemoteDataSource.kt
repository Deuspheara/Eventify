package fr.event.eventify.data.datasource.event.remote

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.OrderBy
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
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
    suspend fun getEvents(page: Int, limit: Int, orderBy: FilterEvent?, category: CategoryEvent?): Flow<Resource<List<Event>>>
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

    override suspend fun getEvents(page: Int, limit: Int, orderBy: FilterEvent?, category: CategoryEvent?): Flow<Resource<List<Event>>> = flow {
        emit(Resource.Loading())
        Log.d(TAG, "Getting events page $page with limit $limit")
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                emit(Resource.Error(message = "User not connected"))
            }else {
                val events = firebaseFirestore.collection("Events")
                    .orderBy(orderBy.toString())
                    .startAfter(page.toLong())
                    .limit(limit.toLong())
                    .get()
                    .await()
                    .toObjects(Event::class.java)
                emit(Resource.Success(events))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while getting events", e)
            emit(Resource.Error(
                message = e.message ?: "Error while getting events",
            ))
            throw e
        }
    }.flowOn(ioContext)

}