package fr.event.eventify.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.domain.event.SetCurrentEventUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val setCurrentEventUseCase: SetCurrentEventUseCase
) : ViewModel(){

        private companion object {
            private const val TAG = "EventDetailsViewModel"
        }

        fun setCurrentEvent(event: EventLight){
            viewModelScope.launch {
                setCurrentEventUseCase(event)
            }
        }
}