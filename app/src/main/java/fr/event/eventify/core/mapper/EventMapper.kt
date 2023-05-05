package fr.event.eventify.core.mapper

//EventMapper

import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.core.models.event.remote.Event
import java.time.LocalDateTime
import java.time.ZoneId

object EventMapper {
    fun mapToEventLight(event: Event): EventLight {
        val date = LocalDateTime.ofInstant(event.date?.toDate()?.toInstant(), ZoneId.systemDefault())
        val frenchDay = when (date.dayOfWeek.toString()) {
            "MONDAY" -> "Lundi"
            "TUESDAY" -> "Mardi"
            "WEDNESDAY" -> "Mercredi"
            "THURSDAY" -> "Jeudi"
            "FRIDAY" -> "Vendredi"
            "SATURDAY" -> "Samedi"
            "SUNDAY" -> "Dimanche"
            else -> "Lundi"
        }
        val frenchMonth = when (date.month.toString()) {
            "JANUARY" -> "Janvier"
            "FEBRUARY" -> "Février"
            "MARCH" -> "Mars"
            "APRIL" -> "Avril"
            "MAY" -> "Mai"
            "JUNE" -> "Juin"
            "JULY" -> "Juillet"
            "AUGUST" -> "Août"
            "SEPTEMBER" -> "Septembre"
            "OCTOBER" -> "Octobre"
            "NOVEMBER" -> "Novembre"
            "DECEMBER" -> "Décembre"
            else -> "Janvier"
        }

        val dateMinute = if (date.minute < 10) "0${date.minute}" else date.minute


        return EventLight(
            id = event.id,
            name = event.name,
            author = event.author,
            description = event.description,
            date = "$frenchDay ${date.dayOfMonth} $frenchMonth ${date.hour}h${dateMinute}",
            location = event.location?.name,
            image = event.image,
            ticketPrice = event.ticketPrice?.amount,
            nbTickets = event.nbTickets,
            nbParticipants = event.participants?.size,
            categoryEvent = event.categoryEvent?.name
        )
    }

    fun mapEventListToEventLightList(eventList: List<Event>): List<EventLight> {
        return eventList.map { mapToEventLight(it) }
    }
}