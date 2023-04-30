package fr.event.eventify.domain.storage

import android.util.Log
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.repository.storage.StorageRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeletePhotoUseCase @Inject constructor(
    private val storageRepository: StorageRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private companion object {
        private const val TAG = "DeletePhotoUseCase"
    }

    suspend operator fun invoke(url: String, collection: String) : Flow<Resource<Unit>> {
        return try {
            storageRepository.deleteFile(url, collection).flowOn(ioDispatcher)
        } catch (e: Exception) {
            Log.e(TAG, "Error while deleting file", e)
            throw e
        }
    }
}