package fr.event.eventify.data.datasource.auth.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.remote.RemoteUser
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

interface AuthRemoteDataSource {

    /**
     * Create a new user with email and password with Firebase Authentication
     * @param email used to provide the User email
     * @param password used to provide the password
     * @return a [Flow] of [FirebaseUser]
     */
    suspend fun createFirebaseUserWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>>

    /**
     * Add user in firestore
     * @param remoteUser informations of user created with firebase auth
     * @return a [Flow] of [Boolean]
     */
    suspend fun createFirestoreUser(remoteUser : RemoteUser) : Flow<Resource<RemoteUser>>
}

class AuthRemoteDataSourceImpl @Inject constructor(
    @DispatcherModule.DispatcherIO private val ioContext : CoroutineDispatcher,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthRemoteDataSource {

    private companion object {
        private const val TAG = "AuthRemoteDataSource"
    }

    override suspend fun createFirebaseUserWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>> = flow {
        withContext(ioContext){
            emit(
                Resource.Loading()
            )
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                if(result.user == null){
                    Log.e(TAG, "Error while creating new user with email: $email and password $password, error: Firebase returned a null user")
                    emit(
                        Resource.Error(message = "Firebase return a null user", data = null)
                    )
                } else {
                    emit(
                        Resource.Success(data= result.user!!)
                    )
                }
            } catch (e: Exception){
                Log.e(TAG, "Error while creating new user with email: $email and password $password, error: $e")
                emit(
                    Resource.Error(message = "Error while creating new user")
                )
                throw e
            }
        }
    }

    override suspend fun createFirestoreUser(remoteUser : RemoteUser) : Flow<Resource<RemoteUser>> = callbackFlow {
        trySend(
            Resource.Loading()
        )
        try{
            val result = firebaseFirestore.collection("User")
                .document(remoteUser.uuid)
                .set(remoteUser)
                .addOnSuccessListener {
                    trySend(
                        Resource.Success(data = remoteUser)
                    )
                }
                .addOnFailureListener {
                    trySend(
                        Resource.Error(message = "Error while storing user in collection User")
                    )
                }
        } catch (e: Exception){
            Log.e(TAG, "Error while storing user in collection User, in document ${remoteUser.uuid}, error: $e")
            trySend(
                Resource.Error(message = "Error while storing user in collection User")
            )
            throw e
        }
    }


}