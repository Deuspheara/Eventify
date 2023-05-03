package fr.event.eventify.ui.payment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.domain.event.GetCurrentEventUseCase
import javax.inject.Inject

@HiltViewModel
class ParticipantViewModel @Inject constructor(
    private val getCurrentEventUseCase: GetCurrentEventUseCase
) : ViewModel() {

    fun getCurrentEvent(): EventLight? {
        return getCurrentEventUseCase()
    }



}