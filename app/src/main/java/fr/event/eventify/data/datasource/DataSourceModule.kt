package fr.event.eventify.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.datasource.auth.remote.AuthRemoteDataSource
import fr.event.eventify.data.datasource.auth.remote.AuthRemoteDataSourceImpl
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideAuthRemoteDataSource(
        @DispatcherModule.DispatcherIO ioContext: CoroutineDispatcher,
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): AuthRemoteDataSource {
        return AuthRemoteDataSourceImpl(
            ioContext = ioContext,
            firebaseAuth = firebaseAuth,
            firebaseFirestore = firebaseFirestore
        )
    }

}
