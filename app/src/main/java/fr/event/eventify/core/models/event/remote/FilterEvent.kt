package fr.event.eventify.core.models.event.remote

enum class FilterEvent {
    NAME,
    DATE,
    PRICE;

    companion object {
        /**
         * Get the filter from a string
         * @param filter the string to convert
         * @return the filter
         */
        fun fromString(filter: String): FilterEvent {
            return when (filter) {
                "Date" -> DATE
                "Price" -> PRICE
                else -> NAME
            }
        }

        /**
         * Get the string from a filter
         * @param filterEvent the filter to convert
         * @return the string
         */
        fun toString(filterEvent: FilterEvent): String {
            return when (filterEvent) {
                NAME -> "Name"
                DATE -> "Date"
                PRICE -> "Price"
                else -> "Name"
            }
        }
    }

}