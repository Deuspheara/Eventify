package fr.event.eventify.ui.profile_detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.domain.payment.GetTransactionsByEventIdUseCase
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class OrganisedEventDetailViewModel @Inject constructor(
    private val getTransactionsByEventIdUseCase: GetTransactionsByEventIdUseCase
): ViewModel(){

    private val _transactionList = MutableStateFlow(TransactionListState())
    val transactionList: MutableStateFlow<TransactionListState> = _transactionList

    private companion object {
        private const val TAG = "OrganisedEventDetailViewModel"
    }

    suspend fun getTransactionsByEventId(eventId: String) {
        try {
            viewModelScope.launch {
                getTransactionsByEventIdUseCase(eventId).collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            _transactionList.value = TransactionListState(isLoading = true)
                        }
                        is Resource.Success -> {
                            _transactionList.value = TransactionListState(transactions = it.data ?: emptyList())
                        }
                        is Resource.Error -> {
                            _transactionList.value = TransactionListState(error = it.message ?: "Unknown error")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while getting user", e)
            throw e
        }
    }

}