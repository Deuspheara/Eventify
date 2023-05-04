package fr.event.eventify.core.models.payment.local

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.gson.annotations.SerializedName

data class Participant(

    @SerializedName("firstName")
    var firstName: String = "",

    @SerializedName("lastName")
    var lastName: String = "",

    @SerializedName("email")
    var email: String = ""
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Participant> {
        override fun createFromParcel(parcel: Parcel): Participant {
            return Participant(parcel)
        }

        override fun newArray(size: Int): Array<Participant?> {
            return arrayOfNulls(size)
        }
    }


}
