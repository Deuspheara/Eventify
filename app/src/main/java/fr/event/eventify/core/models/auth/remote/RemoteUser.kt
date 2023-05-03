package fr.event.eventify.core.models.auth.remote

import com.google.gson.annotations.SerializedName

data class RemoteUser(

    /** Id of the remote resource */
    @SerializedName("uuid")
    val uuid : String,

    /** Full name of this user */
    @SerializedName("displayName")
    val displayName : String,

    /** Pseudo of this user */
    @SerializedName("pseudo")
    val pseudo : String,

    /** Email of this user */
    @SerializedName("email")
    val email : String,

    /** Phone number of this user */
    @SerializedName("phoneNumber")
    val phoneNumber : String,

    /** Url of the profile picture */
    @SerializedName("photoUrl")
    val photoUrl: String,

    /** Which provider is used to log in */
    @SerializedName("providerID")
    val providerID : String,

    /** Check if email is verified */
    @SerializedName("isEmailVerified")
    val isEmailVerified : Boolean,

    /** Event created by this user */
    @SerializedName("createdEvents")
    val createdEvents : List<String>,

    /** Event joined by this user */
    @SerializedName("joinedEvents")
    val joinedEvents : List<String>
){
    constructor() : this("", "", "", "", "", "", "", false, emptyList(), emptyList())
}