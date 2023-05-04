package fr.event.eventify.core.models.payment.remote

import com.google.firebase.firestore.DocumentId
import com.google.gson.annotations.SerializedName
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.payment.local.Participant

data class Transaction (
    @DocumentId
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("eventId")
    val eventId: String? = null,

    @SerializedName("amount")
    val amount: Double? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("transactionType")
    val transactionType: TransactionType? = null,

    @SerializedName("receiver")
    val receiver: String? = null,

    @SerializedName("sender")
    val user: String? = null,

    @SerializedName("participants")
    val participants: List<Participant>? = null,
)