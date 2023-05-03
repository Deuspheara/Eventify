package fr.event.eventify.domain.event

import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.data.repository.event.EventRepository
import javax.inject.Inject

class SetCurrentEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
){

    private companion object {
        private const val TAG = "SetCurrentEventUseCase"
    }

    operator fun invoke(event: EventLight){
        eventRepository.setCurrentEvent(event)
    }
}