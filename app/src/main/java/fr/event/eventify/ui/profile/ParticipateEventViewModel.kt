package fr.event.eventify.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.domain.event.GetJoinedEventsUseCase
import fr.event.eventify.ui.home.EventListState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ParticipateEventViewModel @Inject constructor(
    private val getJoinedEventsUseCase: GetJoinedEventsUseCase
) : ViewModel() {

    private companion object {
        private const val TAG = "ParticipateEventViewModel"
    }

    private val _eventList = MutableStateFlow(EventListState())
    val eventList: StateFlow<EventListState> = _eventList

    suspend fun getJoinedEvents() {
        _eventList.value = EventListState(isLoading = true)
        getJoinedEventsUseCase().collectLatest {
            when(it) {
                is Resource.Success -> {
                    _eventList.value = EventListState(data = it.data)
                }
                is Resource.Error -> {
                    _eventList.value = EventListState(error = it.message ?: "Error while getting joined events")
                }
                is Resource.Loading -> {
                    _eventList.value = EventListState(isLoading = true)
                }
            }
        }
    }
}