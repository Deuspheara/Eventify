package fr.event.eventify.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.domain.event.AddParticipantToEventUseCase
import fr.event.eventify.domain.event.GetCurrentEventUseCase
import fr.event.eventify.ui.home.EventState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentSummaryViewModel @Inject constructor(
    private val currentEventUseCase: GetCurrentEventUseCase,
    private val addParticipantToEventUseCase: AddParticipantToEventUseCase
) : ViewModel() {

    private val _event = MutableStateFlow(EventState())
    val event: StateFlow<EventState> = _event
    fun getCurrentEvent() : EventLight? {
        return currentEventUseCase()
    }

    suspend fun addParticipantToEvent(eventId: String, listParticipants :  List<Participant>) {
        viewModelScope.launch {
            addParticipantToEventUseCase(eventId, listParticipants).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _event.value = EventState(data = it.data)
                    }
                    is Resource.Error -> {
                        _event.value = EventState(error = it.message ?: "An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _event.value = EventState(isLoading = true)
                    }
                }

            }
        }
    }

}