package fr.event.eventify.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.event.eventify.data.repository.auth.AuthRepository
import fr.event.eventify.data.repository.auth.AuthRepositoryImpl
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.data.repository.event.EventRepositoryImpl
import fr.event.eventify.data.repository.payment.PaymentRepository
import fr.event.eventify.data.repository.payment.PaymentRepositoryImpl
import fr.event.eventify.data.repository.storage.StorageRepository
import fr.event.eventify.data.repository.storage.StorageRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindEventRepository(
        impl: EventRepositoryImpl
    ): EventRepository

    @Binds
    abstract fun bindStorageRepository(
        impl: StorageRepositoryImpl
    ): StorageRepository

    @Binds
    abstract fun bindPaymentRepository(
        impl: PaymentRepositoryImpl
    ): PaymentRepository


}