package fr.event.eventify.data.datasource.storage.remote

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

interface StorageRemoteDataSource {
    fun uploadPhoto(bitmap: Bitmap): Flow<Resource<String>>
}

class StorageRemoteDataSourceImpl @Inject constructor(
    @DispatcherModule.DispatcherIO private val ioContext: CoroutineDispatcher,
    private val firebaseStorage: FirebaseStorage
) : StorageRemoteDataSource {

    override fun uploadPhoto(bitmap: Bitmap): Flow<Resource<String>> = callbackFlow {
        val storageRef = firebaseStorage.reference
        val photoRef = storageRef.child("Events/${FirebaseAuth.getInstance().currentUser?.uid}/${System.currentTimeMillis()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val data = baos.toByteArray()

        val uploadTask = photoRef.putBytes(data)

        trySend(Resource.Loading())

        uploadTask.addOnSuccessListener {
            val downloadUrlTask = photoRef.downloadUrl
            downloadUrlTask.addOnSuccessListener { uri ->
                trySend(
                    Resource.Success(
                        uri.toString()
                    )
                )
            }
            downloadUrlTask.addOnFailureListener { exception ->
                trySend(
                    Resource.Error(
                        exception.message ?: "Error while uploading photo"
                    )
                )
                throw exception
            }
        }
        uploadTask.addOnFailureListener { exception ->
            trySend(
                Resource.Error(
                    exception.message ?: "Error while uploading photo"
                )
            )
            throw exception
        }
        awaitClose()
    }.flowOn(ioContext)

}

