package fr.event.eventify.ui.payment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.domain.event.GetCurrentEventUseCase
import javax.inject.Inject

@HiltViewModel
class PaymentSummaryViewModel @Inject constructor(
    private val currentEventUseCase: GetCurrentEventUseCase
) : ViewModel() {

    fun getCurrentEvent() : EventLight? {
        return currentEventUseCase()
    }

}