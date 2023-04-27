package fr.event.eventify.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.google.firebase.firestore.ktx.toObjects

/**
 * This class is to create a paging source for the events
 * @param getEvents the function to get the events
 * @param dispatcher the dispatcher to use
 * @return a [PagingSource] of [Event]
 * @see PagingSource
 */
class EventPagingSource @Inject constructor(
    private val getEvents: suspend (lastSnapshot: DocumentSnapshot?, limit: Int) -> QuerySnapshot,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) : PagingSource<DocumentSnapshot, Event>() {

    private companion object {
        private const val INITIAL_PAGE_NUMBER = 0
        private const val PAGE_SIZE = 20
        private const val TAG = "EventPagingSource"
    }

    /**
     * Load the events
     * @param params the params to use
     * @return a [LoadResult]
     * @see LoadResult
     */
    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Event> {
        return try {
            val lastSnapshot = params.key

            // Step 1
            val eventsResource = getEvents(
                lastSnapshot,
                params.loadSize
            )

            // Step 2
            val events = eventsResource.toObjects<Event>()

            // Step 3
            val nextKey = eventsResource.documents.lastOrNull()

            // Step 4
            LoadResult.Page(
                data = events,
                prevKey = null,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }






    /**
     * Get the refresh key
     * @param state the state to use
     * @return the refresh key
     * @see PagingState
     */

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Event>): DocumentSnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }

    }

}