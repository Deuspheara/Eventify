package fr.event.eventify.data.repository.storage

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.datasource.storage.remote.StorageRemoteDataSource
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface StorageRepository {
    fun uploadPhoto(bitmap: Bitmap): Flow<Resource<String>>
}

class StorageRepositoryImpl @Inject constructor(
    private val storageRemoteDataSource: StorageRemoteDataSource,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : StorageRepository {

    override fun uploadPhoto(bitmap: Bitmap): Flow<Resource<String>> {
        return storageRemoteDataSource.uploadPhoto(bitmap).flowOn(ioDispatcher)
    }
}


