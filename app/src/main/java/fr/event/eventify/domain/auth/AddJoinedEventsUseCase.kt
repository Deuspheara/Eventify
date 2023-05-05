package fr.event.eventify.domain.auth

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.data.datasource.auth.remote.AuthRemoteDataSource
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddJoinedEventsUseCase @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        private const val TAG = "AddJoinedEventsUseCase"
    }

    suspend operator fun invoke(joinedEvents: List<RemoteUser.JoinedEvent>): Flow<Resource<RemoteUser>>{
        return try {
            authRemoteDataSource.addJoinedEvents(joinedEvents)
        } catch (e: Exception) {
            Log.e(TAG, "Error while adding joined event $e")
            throw e
        }
    }
}