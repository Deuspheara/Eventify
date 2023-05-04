package fr.event.eventify.domain.payment

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.data.repository.payment.PaymentRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: PaymentRepository,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) {

    private companion object {
        const val TAG = "AddTransactionUseCase"
    }

    suspend operator fun invoke(transaction: Transaction) : Flow<Resource<Transaction>> {
        return withContext(dispatcher) {
            try {
                repository.addTransaction(transaction)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add transaction", e)
                throw e
            }
        }
    }
}