package fr.event.eventify.domain.storage

import android.graphics.Bitmap
import fr.event.eventify.core.coroutine.DispatcherModule
import fr.event.eventify.data.repository.storage.StorageRepository
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UploadPhotoUseCase @Inject constructor(
    private val storageRepository: StorageRepository,
    @DispatcherModule.DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "UploadPhotoUseCase"
    }

    suspend operator fun invoke(bitmap: Bitmap, collection: String): Flow<Resource<String>> {
        return try {
            storageRepository.uploadPhoto(bitmap, collection).flowOn(ioDispatcher)
        } catch (e: Exception) {
            throw e
        }
    }
}


