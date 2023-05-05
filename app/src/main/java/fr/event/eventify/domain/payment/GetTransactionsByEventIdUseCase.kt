package fr.event.eventify.domain.payment

import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.data.repository.payment.PaymentRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTransactionsByEventIdUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    @DispatcherModule.DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(eventId: String): Flow<Resource<List<Transaction>>> {
        return withContext(dispatcher) {
            try {
                paymentRepository.getTransactionsByEventId(eventId)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}