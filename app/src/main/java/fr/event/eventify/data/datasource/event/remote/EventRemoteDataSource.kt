package fr.event.eventify.data.datasource.event.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.Event
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

}