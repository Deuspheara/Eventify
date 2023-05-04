package fr.event.eventify.data.datasource.payments.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paypal.checkout.createorder.CurrencyCode
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.core.models.payment.remote.TransactionType
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


interface PaymentRemoteDataSource{
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

class PaymentRemoteDataSourceImpl @Inject constructor(
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : PaymentRemoteDataSource {
    override suspend fun addTransaction(
        transaction: Transaction
    ): Flow<Resource<Transaction>> = callbackFlow<Resource<Transaction>> {
        trySend(Resource.Loading())
        try {
            firebaseFirestore.collection("Transactions")
                .document(transaction.eventId ?: "")
                .collection("Transactions")
                .document()
                .set(transaction)
                .addOnSuccessListener {
                    trySend(Resource.Success(transaction))
                }
                .addOnFailureListener {
                    trySend(Resource.Error(it.message ?: "Unknown error"))
                }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Unknown error"))
            throw e
        }
        awaitClose()
    }


}