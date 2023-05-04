package fr.event.eventify.ui.create_event

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.domain.auth.GetUserUsecase
import fr.event.eventify.domain.auth.IsConnectedUseCase
import fr.event.eventify.domain.event.CreateEventUseCase
import fr.event.eventify.domain.storage.UploadPhotoUseCase
import fr.event.eventify.ui.home.EventState
import fr.event.eventify.ui.register.RemoteState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UploadState(
    val isLoading: Boolean = false,
    val data: String? = null,
    val error: String? = null
)

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val isConnectedUseCase: IsConnectedUseCase,
    private val getUserUsecase: GetUserUsecase
) : ViewModel(){

    private val _event = MutableStateFlow(EventState())
    val event: StateFlow<EventState> = _event

    private val _upload = MutableStateFlow(UploadState())
    val upload: StateFlow<UploadState> = _upload

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _user = MutableStateFlow(RemoteState())
    val user: MutableStateFlow<RemoteState> = _user


    init {
        viewModelScope.launch {
            isConnectedUseCase().collectLatest {
                _isConnected.value = it
            }
        }
    }


    fun createEvent(event: Event) {
        viewModelScope.launch {
            if(isConnected.value){
                _event.value = EventState(isLoading = true)
                createEventUseCase(event).collectLatest{
                    when(it){
                        is Resource.Success -> {
                            _event.value = EventState(data = it.data)
                        }
                        is Resource.Error -> {
                            _event.value = EventState(error = it.message ?: "Error while creating event")
                        }
                        else -> {
                            _event.value = EventState(error = "Error while creating event")
                        }
                    }
                }
            }else{
                _event.value = EventState(error = "You must be connected to create an event")
            }
        }
    }

    fun uploadPhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _upload.value = UploadState(isLoading = true)
            uploadPhotoUseCase(bitmap, "Events").collectLatest{
                when(it){
                    is Resource.Success -> {
                        _upload.value = UploadState(data = it.data)
                    }
                    is Resource.Error -> {
                        _upload.value = UploadState(error = it.message ?: "Error while uploading photo")
                    }
                    else -> {
                        _upload.value = UploadState(error = "Error")
                    }
                }
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            getUserUsecase().collectLatest {
                when(it){
                    is Resource.Success -> {
                        _user.value = RemoteState(data = it.data)
                    }
                    is Resource.Error -> {
                        _user.value = RemoteState(error = it.message ?: "Error while getting user")
                    }
                    else -> {
                        _user.value = RemoteState(error = "Error")
                    }
                }
            }
        }
    }
}