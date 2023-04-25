package fr.event.eventify.core.coroutine

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DispatcherDefault

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DispatcherMain

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DispatcherIO

    @DispatcherIO
    @Provides
    fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO

    @DispatcherDefault
    @Provides
    fun provideDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

    @DispatcherMain
    @Provides
    fun provideDispatcherMain(): CoroutineDispatcher = Dispatchers.Main
}

