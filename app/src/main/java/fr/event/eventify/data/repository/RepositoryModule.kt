package fr.event.eventify.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.event.eventify.data.repository.auth.AuthRepository
import fr.event.eventify.data.repository.auth.AuthRepositoryImpl
import fr.event.eventify.data.repository.event.EventRepository
import fr.event.eventify.data.repository.event.EventRepositoryImpl

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


}