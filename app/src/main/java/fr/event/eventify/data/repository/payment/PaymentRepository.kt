package fr.event.eventify.data.repository.payment

import android.util.Log
import com.paypal.checkout.createorder.CurrencyCode
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.core.models.payment.remote.TransactionType
import fr.event.eventify.data.datasource.payments.remote.PaymentRemoteDataSource
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PaymentRepository {
    /**
     * Add a new transaction
     * @param eventId the id of the event
     * @param userId the id of the user
     * @param transactionType the [TransactionType] of the transaction
     * @param amount the amount of the transaction
     * @param currency the [CurrencyCode] of the transaction
     * @param receiver the id of the receiver
     * @param sender the id of the sender
     * @param participants the list of [Participant] of the transaction
     * @return a [Flow] of [Resource]
     * @see TransactionType
     * @see Participant
     */
    suspend fun addTransaction(
        transaction: Transaction
    ) : Flow<Resource<Transaction>>
}

class PaymentRepositoryImpl @Inject constructor(
    private val paymentRemoteDataSource: PaymentRemoteDataSource,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) : PaymentRepository {

    override suspend fun addTransaction(
       transaction: Transaction
    ): Flow<Resource<Transaction>> {
        return withContext(dispatcher) {
            try {
                paymentRemoteDataSource.addTransaction(
                   transaction
                )
            } catch (e: Exception) {
                Log.e("PaymentRepository", "addTransaction: ", e)
                throw e
            }
        }
    }
}