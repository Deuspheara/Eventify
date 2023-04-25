package fr.event.eventify.core.models.event.remote

enum class CategoryEvent {
    /** Concert */
    CONCERT,

    /** Festival */
    FESTIVAL,

    /** Sport */
    SPORT,

    /** Theater */
    THEATER,

    /** Exhibition */
    EXHIBITION,

    /** Other */
    OTHER;

    companion object {
        /**
         * Get the category from a string
         * @param category the string to convert
         * @return the category
         */
        fun fromString(category: String): CategoryEvent {
            return when (category) {
                "Concert" -> CONCERT
                "Festival" -> FESTIVAL
                "Sport" -> SPORT
                "Theater" -> THEATER
                "Exhibition" -> EXHIBITION
                else -> OTHER
            }
        }

        /**
         * Get the string from a category
         * @param categoryEvent the category to convert
         * @return the string
         */
        fun toString(categoryEvent: CategoryEvent): String {
            return when (categoryEvent) {
                CONCERT -> "Concert"
                FESTIVAL -> "Festival"
                SPORT -> "Sport"
                THEATER -> "Theater"
                EXHIBITION -> "Exhibition"
                else -> "Other"
            }
        }
    }
}
