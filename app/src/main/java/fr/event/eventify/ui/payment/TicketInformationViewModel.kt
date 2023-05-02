package fr.event.eventify.ui.payment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.domain.event.GetCurrentEventUseCase
import javax.inject.Inject

@HiltViewModel
class TicketInformationViewModel @Inject constructor(
    private val getCurrentEventUseCase: GetCurrentEventUseCase
) : ViewModel() {

    private companion object {
        private const val TAG = "PaymentViewModel"
    }


    fun getCurrentEvent(): EventLight? {
        return getCurrentEventUseCase()
    }
}