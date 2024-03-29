package fr.event.eventify.data.datasource.auth.remote

import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
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

    /**
     * Sign in with email and password
     * @param email used to provide the User email
     * @param password used to provide the password
     * @return a [Flow] of [FirebaseUser]
     */
    suspend fun signInWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>>

    /**
     * Sign in with Google
     * @return a [Flow] of [FirebaseUser]
     */
    suspend fun signInWithGoogle(credential: AuthCredential): Flow<Resource<FirebaseUser>>

    /**
     * Check if user is connected
     * @return a [Flow] of [Boolean]
     * @see [FirebaseAuth.getCurrentUser]
     */
    suspend fun isUserConnected(): Flow<Boolean>

    /**
     * Sign out
     * @return a [Flow] of [Unit]
     * @see [FirebaseAuth.signOut]
     * @see [FirebaseAuth.getCurrentUser]
     */
    suspend fun getUser(): Flow<Resource<RemoteUser>>

    suspend fun modifyUser(uuid: String, pseudo: String, description: String, photoUrl: String): Flow<Resource<RemoteUser>>

    /**
     * Add JoinedEvent in firestore
     * @param joinedEvent informations of joined event
     * @return a [Flow] of [RemoteUser]
     */
    suspend fun addJoinedEvents(joinedEvents: List<RemoteUser.JoinedEvent>): Flow<Resource<RemoteUser>>

    /**
     * Remove JoinedEvent in firestore
     * @param joinedEvent informations of joined event
     */
    suspend fun logout(): Flow<Boolean>
}

class AuthRemoteDataSourceImpl @Inject constructor(
    @DispatcherModule.DispatcherIO private val ioContext: CoroutineDispatcher,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthRemoteDataSource {

    private companion object {
        private const val TAG = "AuthRemoteDataSource"
    }

    override suspend fun createFirebaseUserWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>> = flow {
            emit(
                Resource.Loading()
            )
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                if (result.user == null) {
                    Log.e(
                        TAG,
                        "Error while creating new user with email: $email and password $password, error: Firebase returned a null user"
                    )
                    emit(
                        Resource.Error(message = "Firebase return a null user", data = null)
                    )
                } else {
                    emit(
                        Resource.Success(data = result.user!!)
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error while creating new user with email: $email and password $password, error: $e"
                )
                emit(
                    Resource.Error(message = "Error while creating new user")
                )
            }
        }

    override suspend fun createFirestoreUser(remoteUser: RemoteUser): Flow<Resource<RemoteUser>> = callbackFlow<Resource<RemoteUser>> {
        trySend(Resource.Loading())
        Log.d(TAG, "Storing user in collection User, in document ${remoteUser.uuid}")
        try {
                Log.d(TAG, "Storing user in collection User, in document ${remoteUser.uuid}")
            firebaseFirestore.collection("User")
                .document(remoteUser.uuid)
                .set(remoteUser)
                .addOnSuccessListener {
                    trySend(Resource.Success(data = remoteUser))
                }
                .addOnFailureListener {
                    trySend(Resource.Error(message = "Error while storing user in collection User"))
                }
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error while storing user in collection User, in document ${remoteUser.uuid}, error: $e")
                trySend(Resource.Error(message = "Error while storing user in collection User"))
            throw e
        }
        awaitClose()
    }.flowOn(ioContext)

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Flow<Resource<FirebaseUser>> = flow {
            emit(
                Resource.Loading()
            )
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                if (result.user == null) {
                    Log.e(
                        TAG,
                        "Error while signing in with email: $email and password $password, error: Firebase returned a null user"
                    )
                    emit(
                        Resource.Error(message = "Firebase return a null user", data = null)
                    )
                } else {
                    emit(
                        Resource.Success(result.user!!)
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error while signing in with email: $email and password $password, error: $e"
                )
                emit(
                    Resource.Error(message = "Error while signing in")
                )
            }
        }

    override suspend fun signInWithGoogle(credential: AuthCredential): Flow<Resource<FirebaseUser>> = flow {
        emit(Resource.Loading())
        try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            emit(Resource.Success(authResult.user!!))
        } catch (e: ApiException) {
            Log.e(TAG, "Error while signing in with Google: $e")
            emit(Resource.Error(message = "Error while signing in with Google"))
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error while authenticating with Firebase: $e")
            emit(Resource.Error(message = "Error while authenticating with Firebase"))
            throw e
        }
    }.flowOn(ioContext)

    override suspend fun isUserConnected(): Flow<Boolean> = flow {
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Log.d(TAG, "User is not connected")
                emit(false)
            } else {
                Log.d(TAG, "User is connected")
                emit(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while checking if user is connected: $e")
            emit(false)
            throw e
        }
    }.flowOn(ioContext)

    override suspend fun getUser(): Flow<Resource<RemoteUser>> = callbackFlow<Resource<RemoteUser>> {
        trySend(Resource.Loading())
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Log.e(TAG, "Error while getting user, user is null")
                trySend(Resource.Error(message = "Error while getting user, user is null"))
            } else {
                Log.d(TAG, "User is connected")
                firebaseFirestore.collection("User")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            val remoteUser = it.toObject(RemoteUser::class.java)
                            if (remoteUser == null) {
                                Log.e(TAG, "Error while getting user, user is null")
                                trySend(Resource.Error(message = "Error while getting user, user is null"))
                            } else {
                                trySend(Resource.Success(remoteUser))
                            }
                        } else {
                            Log.e(TAG, "Error while getting user, user is null")
                            trySend(Resource.Error(message = "Error while getting user, user is null"))
                        }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Error while getting user, user is null")
                        trySend(Resource.Error(message = "Error while getting user, user is null"))
                    }
                    .await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while getting user: $e")
            trySend(Resource.Error(message = "Error while getting user"))
            throw e
        }
        awaitClose()
    }.flowOn(ioContext)

    override suspend fun modifyUser(uuid: String, pseudo: String, description: String, photoUrl: String): Flow<Resource<RemoteUser>> = callbackFlow<Resource<RemoteUser>>{
        trySend(Resource.Loading())

        try {
            val user = firebaseFirestore.collection("User").document(uuid)

            user
                .update(
                    mapOf(
                        "pseudo" to pseudo,
                        "photoUrl" to photoUrl
                    )
                )
                .await()

        }catch (e: Exception) {
            Log.e(TAG, "Error while modifying user: $e")
            trySend(Resource.Error(message = "Error while modifying user"))
            throw e
        }
        awaitClose()
    }.flowOn(ioContext)



    override suspend fun addJoinedEvents(joinedEvents: List<RemoteUser.JoinedEvent>): Flow<Resource<RemoteUser>> {
        return callbackFlow<Resource<RemoteUser>> {
            trySend(Resource.Loading())
            try {
                val user = firebaseAuth.currentUser
                if (user == null) {
                    Log.e(TAG, "Error while adding joined event, user is null")
                    trySend(Resource.Error(message = "Error while adding joined event, user is null"))
                } else {
                    Log.d(TAG, "User is connected")
                    firebaseFirestore.collection("User")
                        .document(user.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            val currentUser = document.toObject(RemoteUser::class.java)
                            if (currentUser == null) {
                                Log.e(TAG, "Error while adding joined event, user data is null")
                                trySend(Resource.Error(message = "Error while adding joined event, user data is null"))
                            } else {
                                val updatedEvents = currentUser.joinedEvents + joinedEvents
                                firebaseFirestore.collection("User")
                                    .document(user.uid)
                                    .update("joinedEvents", updatedEvents)
                                    .addOnSuccessListener {
                                        trySend(Resource.Success(currentUser.copy(joinedEvents = updatedEvents)))
                                        close() // close the flow when done
                                    }
                                    .addOnFailureListener {
                                        Log.e(TAG, "Error while adding joined event: $it")
                                        trySend(Resource.Error(message = "Error while adding joined event"))
                                        close() // close the flow when done
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Log.e(TAG, "Error while getting user data: $it")
                            trySend(Resource.Error(message = "Error while adding joined event"))
                            close() // close the flow when done
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error while adding joined event: $e")
                trySend(Resource.Error(message = "Error while adding joined event"))
                throw e
            }
            awaitClose()
        }.flowOn(ioContext)
    }

    override suspend fun logout(): Flow<Boolean> {
        return flow {
            try {
                firebaseAuth.signOut()
                emit(true)
            } catch (e: Exception) {
                Log.e(TAG, "Error while logging out: $e")
                emit(false)
                throw e
            }
        }.flowOn(ioContext)
    }


}