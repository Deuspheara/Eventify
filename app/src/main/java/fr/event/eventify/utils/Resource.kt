package fr.event.eventify.utils

sealed class Resource<T>(val data: T? = null, val message: String? = null) {

    /** If the flow succeeded */
    class Success<T>(data: T) : Resource<T>(data)

    /** If the flow return an error */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /** If the flow is still loading */
    class Loading<T>(data: T? = null) : Resource<T>(data)

}