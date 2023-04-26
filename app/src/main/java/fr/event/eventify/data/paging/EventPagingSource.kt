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
import kotlinx.coroutines.flow.first
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
    private val getEvents: suspend(page: Int, limit: Int, orderBy: FilterEvent?, category: CategoryEvent?) -> Flow<Resource<List<Event>>>,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val orderBy: FilterEvent?,
    private val category: CategoryEvent?
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
                val response = getEvents(pageNumber, PAGE_SIZE, orderBy, category).first()
                if (response is Resource.Success) {
                    LoadResult.Page(
                        data = response.data ?: emptyList(),
                        prevKey = if (pageNumber == INITIAL_PAGE_NUMBER) null else pageNumber - 1,
                        nextKey = if (response.data?.isEmpty() == true) null else pageNumber + 1
                    )
                } else {
                    LoadResult.Error(Exception("Error while fetching events"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error while fetching events", e)
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