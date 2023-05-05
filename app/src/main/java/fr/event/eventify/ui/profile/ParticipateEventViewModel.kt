package fr.event.eventify.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.domain.event.GetJoinedEventsRawUseCase
import fr.event.eventify.domain.event.GetJoinedEventsUseCase
import fr.event.eventify.ui.home.EventListState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

data class JoinedEventRawState(
    val data: List<RemoteUser.JoinedEvent>? = null,
    val error: String = "",
    val isLoading: Boolean = false
)
@HiltViewModel
class ParticipateEventViewModel @Inject constructor(
    private val getJoinedEventsUseCase: GetJoinedEventsUseCase,
    private val getJoinedEventsRawUseCase: GetJoinedEventsRawUseCase
) : ViewModel() {

    private companion object {
        private const val TAG = "ParticipateEventViewModel"
    }

    private val _eventList = MutableStateFlow(EventListState())
    val eventList: StateFlow<EventListState> = _eventList

    private val _eventListRaw = MutableStateFlow(JoinedEventRawState())
    val eventListRaw: StateFlow<JoinedEventRawState> = _eventListRaw

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

    suspend fun getJoinedEventsRaw() {
        _eventListRaw.value = JoinedEventRawState(isLoading = true)
        getJoinedEventsRawUseCase().collectLatest {
            when(it) {
                is Resource.Success -> {
                    _eventListRaw.value = JoinedEventRawState(data = it.data)
                }
                is Resource.Error -> {
                    _eventListRaw.value = JoinedEventRawState(error = it.message ?: "Error while getting joined events raw")
                }
                is Resource.Loading -> {
                    _eventListRaw.value = JoinedEventRawState(isLoading = true)
                }
            }
        }
    }
}