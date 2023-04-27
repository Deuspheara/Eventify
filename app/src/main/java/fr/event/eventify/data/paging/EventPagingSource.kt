package fr.event.eventify.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
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

/**
 * This class is to create a paging source for the events
 * @param getEvents the function to get the events
 * @param dispatcher the dispatcher to use
 * @return a [PagingSource] of [Event]
 * @see PagingSource
 */
class EventPagingSource @Inject constructor(
    private val getEvents: suspend (page: Int, limit: Int) -> Resource<List<Event>>,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) : PagingSource<Int, Event>() {

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
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Event> {
        return withContext(dispatcher){
            try {
                val pageNumber = params.key ?: INITIAL_PAGE_NUMBER
                val events = getEvents(pageNumber, PAGE_SIZE)
                Log.d(TAG, "load: $events")
                if (events == null) {
                    LoadResult.Error(Exception("No more events available"))
                } else {
                    when (events) {
                        is Resource.Success -> {
                            val data = events.data ?: emptyList()
                            Log.d(TAG, "load: $data")
                            LoadResult.Page(
                                data = data,
                                prevKey = if (pageNumber == INITIAL_PAGE_NUMBER) null else pageNumber - 1,
                                nextKey = if (data.isEmpty()) null else pageNumber + 1
                            )
                        }
                        is Resource.Loading -> LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                        is Resource.Error -> LoadResult.Error(Exception(events.message))
                    }
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }




    /**
     * Get the refresh key
     * @param state the state to use
     * @return the refresh key
     * @see PagingState
     */
    override fun getRefreshKey(state: PagingState<Int, Event>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}