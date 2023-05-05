package fr.event.eventify.data.repository.auth

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.data.datasource.auth.remote.AuthRemoteDataSource
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AuthRepository {
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
     * @param idToken used to provide the User idToken
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
     * Get current user
     * @return a [Flow] of [RemoteUser]
     * @see [FirebaseAuth.getCurrentUser]
     */
    suspend fun getUser(): Flow<Resource<RemoteUser>>

    suspend fun modifyUser(uuid: String, pseudo: String, description: String, photoUrl: String): Flow<Resource<RemoteUser>>

    /**
     * Add JoinedEvent in firestore
     * @param joinedEvent informations of joined event
     * @return a [Flow] of [RemoteUser]
     */
    suspend fun addJoinedEvents(joinedEvent: List<RemoteUser.JoinedEvent>): Flow<Resource<RemoteUser>>

    /**
     * Remove JoinedEvent in firestore
     * @param joinedEvent informations of joined event
     */
    suspend fun logout(): Flow<Boolean>
}

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    private companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun createFirebaseUserWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>> {
        return withContext(ioDispatcher) {
            try {
                authRemoteDataSource.createFirebaseUserWithEmail(email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Error while creating new user with email: $email and password $password, error: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun createFirestoreUser(remoteUser: RemoteUser): Flow<Resource<RemoteUser>> {
        return withContext(ioDispatcher) {
            try {
                authRemoteDataSource.createFirestoreUser(remoteUser)
            } catch (e: Exception) {
                Log.e(TAG, "Error while creating new user in firestore, error: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Flow<Resource<FirebaseUser>> {
        return withContext(ioDispatcher) {
            try {
                authRemoteDataSource.signInWithEmail(email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Error while signing in with email: $email and password $password, error: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun signInWithGoogle(credential: AuthCredential): Flow<Resource<FirebaseUser>> {
        return withContext(ioDispatcher) {
            try {
                authRemoteDataSource.signInWithGoogle(credential)
            } catch (e: Exception) {
                Log.e(TAG, "Error while signing in with google, error: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun isUserConnected(): Flow<Boolean> {
        return withContext(ioDispatcher) {
            try {
                authRemoteDataSource.isUserConnected()
            } catch (e: Exception) {
                Log.e(TAG, "Error while checking if user is connected, error: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun getUser(): Flow<Resource<RemoteUser>> {
        return withContext(ioDispatcher) {
            try {
                authRemoteDataSource.getUser()
            } catch (e: Exception) {
                Log.e(TAG, "Error while getting user, error: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun modifyUser(
        uuid: String,
        pseudo: String,
        description: String,
        photoUrl: String
    ): Flow<Resource<RemoteUser>> {
        return withContext(ioDispatcher){
            try {
                authRemoteDataSource.modifyUser(uuid,pseudo,description,photoUrl)
            }catch (e: Exception) {
                Log.e(TAG, "Error while modifying user, error: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun addJoinedEvents(joinedEvent: List<RemoteUser.JoinedEvent>): Flow<Resource<RemoteUser>> {
        return withContext(ioDispatcher) {
            try {
                authRemoteDataSource.addJoinedEvents(joinedEvent)
            } catch (e: Exception) {
                Log.e(TAG, "Error while adding joined event, error: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun logout(): Flow<Boolean> {
        return withContext(ioDispatcher) {
            try {
                authRemoteDataSource.logout()
            } catch (e: Exception) {
                Log.e(TAG, "Error while logout, error: ${e.message}")
                throw e
            }
        }
    }
}