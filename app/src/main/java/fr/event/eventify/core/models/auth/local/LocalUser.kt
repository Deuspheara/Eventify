package fr.event.eventify.core.models.auth.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID

@Entity
data class LocalUser(

    /** Id in this database */
    @PrimaryKey val local_id : Int,

    /** Id of remote resource */
    @ColumnInfo(name = "firestore_uuid")
    val fireStoreUUID: String,

    /** Full name of this user */
    @ColumnInfo(name = "display_name")
    val displayName: String,

    /** Url of the profile photo */
    @ColumnInfo(name = "photo_url")
    val photoUrl: String,

    /** Email of this user */
    @ColumnInfo(name = "email")
    val email: String,

    /** Phone number of this user */
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    /** Which provider is used to log in */
    @ColumnInfo(name = "provider_id")
    val providerId: String,

    /** Check if the email has been verified */
    @ColumnInfo(name = "is_email_verified")
    val isEmailVerified: String,
    )