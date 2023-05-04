package fr.event.eventify.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.domain.auth.AddJoinedEventsUseCase
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.event.AddParticipantToEventUseCase
import fr.event.eventify.domain.event.GetCurrentEventUseCase
import fr.event.eventify.domain.payment.AddTransactionUseCase
import fr.event.eventify.ui.home.EventState
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


data class TransactionState(
    val isLoading: Boolean = false,
    val data: Transaction? = null,
    val error: String? = null
)

data class JoinedEventState(
    val isLoading: Boolean = false,
    val data: RemoteUser? = null,
    val error: String? = null
)

@HiltViewModel
class PaymentSummaryViewModel @Inject constructor(
    private val currentEventUseCase: GetCurrentEventUseCase,
    private val addParticipantToEventUseCase: AddParticipantToEventUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getUserUsecase: GetUserUsecase,
    private val addJoinedEventsUseCase: AddJoinedEventsUseCase
) : ViewModel() {

    private val _event = MutableStateFlow(EventState())
    val event: StateFlow<EventState> = _event

    private val _transaction = MutableStateFlow(TransactionState())
    val transaction: StateFlow<TransactionState> = _transaction

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user

    private val _joinedEvents = MutableStateFlow(JoinedEventState())
    val joinedEvents: MutableStateFlow<JoinedEventState> = _joinedEvents


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

    suspend fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            addTransactionUseCase(transaction).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _transaction.value = TransactionState(data = it.data)
                    }
                    is Resource.Error -> {
                        _transaction.value = TransactionState(error = it.message ?: "An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _transaction.value = TransactionState(isLoading = true)
                    }
                }

            }
        }
    }

    suspend fun getUser() {
        try {
            viewModelScope.launch {
                getUserUsecase().collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            _user.value = RemoteState(isLoading = true)
                        }
                        is Resource.Success -> {
                            _user.value = RemoteState(data = it.data)
                        }
                        is Resource.Error -> {
                            _user.value = RemoteState(error = it.message ?: "Unknown error")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addJoinedEvents(joinedEvents: List<RemoteUser.JoinedEvent>) {
        viewModelScope.launch {
            addJoinedEventsUseCase(joinedEvents).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _joinedEvents.value = JoinedEventState(data = it.data)
                    }
                    is Resource.Error -> {
                        _joinedEvents.value = JoinedEventState(error = it.message ?: "An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _joinedEvents.value = JoinedEventState(isLoading = true)
                    }
                }

            }
        }
    }
}