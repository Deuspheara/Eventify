package fr.event.eventify.core.models.event.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import fr.event.eventify.core.models.payment.local.Participant

data class Event(
    @DocumentId
    @SerializedName("uuid")
    var id: String = "",

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("authorUuid")
    val author: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @ServerTimestamp
    @SerializedName("date")
    val date: Timestamp? = null,

    @SerializedName("location")
    val location: LocationEvent? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("ticketPrice")
    val ticketPrice: PriceEvent? = null,

    @SerializedName("nbTickets")
    val nbTickets: Int? = null,

    @SerializedName("participants")
    val participants: List<Participant>? = null,

    @SerializedName("category")
    val categoryEvent: CategoryEvent? = null,

    @SerializedName("favorite")
    var favorite: Boolean = false,
){
    // Nested classes
    data class LocationEvent(
        @SerializedName("uuid")
        val id: String = "",

        @SerializedName("name")
        val name: String = "",

        @SerializedName("address")
        val address: String? = null,

        @SerializedName("city")
        val city: String? = null,

        @SerializedName("zip_code")
        val zipCode: String? = null,

        @SerializedName("country")
        val country: String? = null,

        @SerializedName("latitude")
        val latitude: Double? = null,

        @SerializedName("longitude")
        val longitude: Double? = null,
    )

    data class PriceEvent(
        @SerializedName("currency")
        val currency: String? = null,

        @SerializedName("amount")
        val amount: Double? = null,
    )
}