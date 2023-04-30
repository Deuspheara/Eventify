package fr.event.eventify.core.models.event.local

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import fr.event.eventify.core.models.event.remote.CategoryEvent

data class EventLight(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("authorUuid")
    val author: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @ServerTimestamp
    @SerializedName("date")
    val date: String? = null,

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("ticketPrice")
    val ticketPrice: Double? = null,

    @SerializedName("nbTickets")
    val nbTickets: Int? = null,

    @SerializedName("nbParticipants")
    val nbParticipants: Int? = null,

    @SerializedName("category")
    val categoryEvent: String? = null,
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(author)
        dest.writeString(description)
        dest.writeString(date)
        dest.writeString(location)
        dest.writeString(image)
        dest.writeDouble(ticketPrice ?: 0.0)
        dest.writeInt(nbTickets ?: 0)
        dest.writeInt(nbParticipants ?: 0)
        dest.writeString(categoryEvent)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventLight> {
        override fun createFromParcel(parcel: Parcel): EventLight {
            return EventLight(parcel)
        }

        override fun newArray(size: Int): Array<EventLight?> {
            return arrayOfNulls(size)
        }
    }
}