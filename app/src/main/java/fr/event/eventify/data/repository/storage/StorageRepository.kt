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
    /**
     * Upload a photo
     * @param bitmap the bitmap to upload
     * @return a [Flow] of [Resource]
     * @see [StorageRemoteDataSource]
     */
    suspend fun uploadPhoto(bitmap: Bitmap, collection: String): Flow<Resource<String>>

    /**
     * Delete a file
     * @param url the url of the file to delete
     * @return a [Flow] of [Resource]
     * @see [StorageRemoteDataSource]
     */
    suspend fun deleteFile(url: String, collection: String): Flow<Resource<Unit>>
}

class StorageRepositoryImpl @Inject constructor(
    private val storageRemoteDataSource: StorageRemoteDataSource,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : StorageRepository {

    override suspend fun uploadPhoto(bitmap: Bitmap, collection: String): Flow<Resource<String>> {
        return try {
            storageRemoteDataSource.uploadPhoto(bitmap, collection).flowOn(ioDispatcher)
        } catch (e: Exception) {
            Log.e(TAG, "Error while uploading photo", e)
            throw e
        }
    }

    override suspend fun deleteFile(url: String, collection: String): Flow<Resource<Unit>> {
        return try {
            storageRemoteDataSource.deleteFile(url, collection).flowOn(ioDispatcher)
        } catch (e: Exception) {
            Log.e(TAG, "Error while deleting file", e)
            throw e
        }
    }

}


