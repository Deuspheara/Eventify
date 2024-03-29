package fr.event.eventify.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.datasource.auth.remote.AuthRemoteDataSource
import fr.event.eventify.data.datasource.auth.remote.AuthRemoteDataSourceImpl
import fr.event.eventify.data.datasource.event.remote.EventRemoteDataSource
import fr.event.eventify.data.datasource.event.remote.EventRemoteDataSourceImpl
import fr.event.eventify.data.datasource.payments.remote.PaymentRemoteDataSource
import fr.event.eventify.data.datasource.payments.remote.PaymentRemoteDataSourceImpl
import fr.event.eventify.data.datasource.storage.remote.StorageRemoteDataSource
import fr.event.eventify.data.datasource.storage.remote.StorageRemoteDataSourceImpl
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {


    @Binds
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    abstract fun bindCharacterRemoteDataSource(
        impl: EventRemoteDataSourceImpl
    ): EventRemoteDataSource

    @Binds
    abstract fun bindStorageRemoteDataSource(
        impl: StorageRemoteDataSourceImpl
    ): StorageRemoteDataSource

    @Binds
    abstract fun bindPaymentRemoteDataSource(
        impl: PaymentRemoteDataSourceImpl
    ): PaymentRemoteDataSource
}
