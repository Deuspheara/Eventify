package fr.event.eventify.domain.event

import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.data.repository.event.EventRepository
import javax.inject.Inject

class GetCurrentEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
){

    private companion object {
        private const val TAG = "GetCurrentEventUseCase"
    }

    operator fun invoke(): EventLight? {
        return eventRepository.getCurrentEvent()
    }
}