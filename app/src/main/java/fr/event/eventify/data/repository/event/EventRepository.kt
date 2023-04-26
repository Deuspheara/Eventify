package fr.event.eventify.data.repository.event

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.Event
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


}

class EventRepositoryImpl @Inject constructor(
    private val EventRemoteDataSource: EventRemoteDataSource,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : EventRepository {

    private companion object {
        private const val TAG = "EventRepository"
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


}