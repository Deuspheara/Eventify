package fr.event.eventify.data.datasource.event.remote

import android.util.Log
import androidx.paging.PagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.core.models.event.remote.stringValue
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.data.paging.EventPagingSource
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
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
    suspend fun getEvents(
        page: Int,
        limit: Int,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): Resource<List<Event>>

    /**
     * Get all events paginated
     * @param name the name of the event to get
     * @param orderBy the [FilterEvent] to apply
     * @param category the [CategoryEvent] to apply
     * @return a [Flow] of [Resource]
     * @see FilterEvent
     * @see CategoryEvent
     */
    fun createCharacterPagingSource(
        name: String?,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): PagingSource<DocumentSnapshot, Event>

    /**
     * Get all events paginated
     * @param lastSnapshot the last [DocumentSnapshot] to get
     * @param limit the limit of events to get
     * @return a [Flow] of [Resource]
     * @see DocumentSnapshot
     */
    suspend fun getEventsQuerySnapshot(
        lastSnapshot: DocumentSnapshot?,
        limit: Int,
        name: String?,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): QuerySnapshot

    /**
     * Add a participant to an event
     * @param eventId the id of the event
     * @param listParticipants the [List] of [Participant] to add
     * @return a [Flow] of [Resource] of [Event]
     * @see Participant
     */
    suspend fun addParticipant(
        eventId: String,
        listParticipants: List<Participant>
    ): Flow<Resource<Event>>

    suspend fun addInterestedUser(
        eventId: String,
        interestedUsers: List<String>
    ): Flow<Resource<Event>>

    suspend fun deleteInterestedUser(
        eventId: String,
        interestedUsers: List<String>
    ): Flow<Resource<Event>>
}

class EventRemoteDataSourceImpl @Inject constructor(
    @DispatcherModule.DispatcherIO private val ioContext: CoroutineDispatcher,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : EventRemoteDataSource {

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
            } else {
                val eventRef = firebaseFirestore.collection("Events").document()
                val eventWithId = event.copy(id = eventRef.id)
                eventRef.set(eventWithId).await()
                emit(Resource.Success(eventWithId))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while creating event", e)
            emit(
                Resource.Error(
                    message = e.message ?: "Error while creating event",
                )
            )
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
            val user = firebaseAuth.currentUser
            if (user == null) {
                return Resource.Error(message = "User not connected")
            } else {
                val events = firebaseFirestore.collection("Events")
                    .orderBy(orderBy?.stringValue ?: FilterEvent.NAME.stringValue)
                    .startAfter(page)
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
        name: String?,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): PagingSource<DocumentSnapshot, Event> {
        return EventPagingSource(
            getEvents = { lastSnapshot, limit ->
                getEventsQuerySnapshot(lastSnapshot, limit, name, orderBy, category)
            },
            dispatcher = ioContext
        )
    }

    override suspend fun getEventsQuerySnapshot(
        lastSnapshot: DocumentSnapshot?,
        limit: Int,
        name: String?,
        orderBy: FilterEvent?,
        category: CategoryEvent?
    ): QuerySnapshot {

        val query = firebaseFirestore.collection("Events")
            .orderBy(orderBy?.stringValue ?: FilterEvent.NAME.stringValue)
            .startAfter(lastSnapshot)
            .limit(limit.toLong())

        if (!name.isNullOrEmpty()) {
            Log.d(TAG, "getEventsQuerySnapshot: $name")
            return query.whereEqualTo("name", name).get().await()
        }

        Log.d(TAG, "getEventsQuerySnapshot: $category")
        return query.get().await()
    }

    override suspend fun addParticipant(
        eventId: String,
        listParticipants: List<Participant>
    ): Flow<Resource<Event>> = flow<Resource<Event>> {
        emit(Resource.Loading())
        Log.d(TAG, "Adding participant $listParticipants")
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                emit(Resource.Error(message = "User not connected"))
            } else {
                val eventRef = firebaseFirestore.collection("Events").document(eventId)
                val transaction = firebaseFirestore.runTransaction { transaction ->
                    val event = transaction.get(eventRef).toObject(Event::class.java)
                    val existingParticipants = event?.participants ?: emptyList()
                    val newParticipants = existingParticipants + listParticipants
                    val updatedEvent = event?.copy(participants = newParticipants)
                    if (updatedEvent != null) {
                        transaction.set(eventRef, updatedEvent)
                        Resource.Success(updatedEvent)
                    } else {
                        Resource.Error(message = "Event not found")
                    }
                }.await()

                emit(transaction)

            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while adding participant", e)
            emit(
                Resource.Error(
                    message = e.message ?: "Error while adding participant",
                )
            )
            throw e
        }
    }.flowOn(ioContext)

    override suspend fun addInterestedUser(
        eventId: String,
        interestedUsers: List<String>
    ): Flow<Resource<Event>> = flow<Resource<Event>> {
        emit(Resource.Loading())
        Log.d(TAG, "Adding interested user $interestedUsers")
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                emit(Resource.Error(message = "User not connected"))
            } else {
                val eventRef = firebaseFirestore.collection("Events").document(eventId)
                val transaction = firebaseFirestore.runTransaction { transaction ->
                    val event = transaction.get(eventRef).toObject(Event::class.java)
                    val existingInterested = event?.interested ?: emptyList()
                    val newInterested = existingInterested + interestedUsers
                    val updatedEvent = event?.copy(interested = newInterested)
                    if (updatedEvent != null) {
                        transaction.set(eventRef, updatedEvent)
                        Resource.Success(updatedEvent)
                    } else {
                        Resource.Error(message = "Event not found")
                    }
                }.await()
                emit(transaction)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while adding interested user", e)
            emit(
                Resource.Error(
                    message = e.message ?: "Error while adding interested user",
                )
            )
            throw e
        }
    }.flowOn(ioContext)

    override suspend fun deleteInterestedUser(
        eventId: String,
        interestedUsers: List<String>
    ): Flow<Resource<Event>> = flow<Resource<Event>> {
        emit(Resource.Loading())
        Log.d(TAG, "deleting interested user $interestedUsers")
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                emit(Resource.Error(message = "User not connected"))
            } else {
                val eventRef = firebaseFirestore.collection("Events").document(eventId)
                val transaction = firebaseFirestore.runTransaction { transaction ->
                    val event = transaction.get(eventRef).toObject(Event::class.java)
                    val existingInterested = event?.interested ?: emptyList()
                    val newInterested = existingInterested - interestedUsers.toSet()
                    val updatedEvent = event?.copy(interested = newInterested)
                    if (updatedEvent != null) {
                        transaction.set(eventRef, updatedEvent)
                        Resource.Success(updatedEvent)
                    } else {
                        Resource.Error(message = "Event not found")
                    }
                }.await()
                emit(transaction)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while deleting interested user", e)
            emit(
                Resource.Error(
                    message = e.message ?: "Error while deleting interested user",
                )
            )
            throw e
        }
    }.flowOn(ioContext)


}