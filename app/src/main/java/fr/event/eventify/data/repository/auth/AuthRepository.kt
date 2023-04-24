package fr.event.eventify.data.repository.auth

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.core.models.remote.RemoteUser
import fr.event.eventify.data.datasource.auth.remote.AuthRemoteDataSource
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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
}