package fr.event.eventify.core.models.event.remote

import com.google.gson.annotations.SerializedName

data class Event(

    @SerializedName("uuid")
    val id: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("authorUuid")
    val author: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("date")
    val date: String?,

    @SerializedName("location")
    val location: LocationEvent?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("ticketPrice")
    val ticketPrice: PriceEvent?,

    @SerializedName("nbTickets")
    val nbTickets: Int?,

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
