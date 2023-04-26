package fr.event.eventify.core.models.event.remote

import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import fr.event.eventify.core.models.auth.remote.RemoteUser
import java.util.Date

data class Event(

    @SerializedName("uuid")
    val id: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("authorUuid")
    val author: String?,

    @SerializedName("description")
    val description: String?,

    @ServerTimestamp
    @SerializedName("date")
    val date: Date?,

    @SerializedName("location")
    val location: LocationEvent?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("ticketPrice")
    val ticketPrice: PriceEvent?,

    @SerializedName("nbTickets")
    val nbTickets: Int?,

    @SerializedName("participants")
    val participants: List<String>?,

    @SerializedName("category")
    val categoryEvent: CategoryEvent?,
){
    data class LocationEvent(

        @SerializedName("uuid")
        val id: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("address")
        val address: String?,

        @SerializedName("city")
        val city: String?,

        @SerializedName("zip_code")
        val zipCode: String?,

        @SerializedName("country")
        val country: String?,

        @SerializedName("latitude")
        val latitude: Double?,

        @SerializedName("longitude")
        val longitude: Double?,
    )

    data class PriceEvent(

        @SerializedName("currency")
        val currency: String?,

        @SerializedName("amount")
        val amount: Double?,
    )

}
