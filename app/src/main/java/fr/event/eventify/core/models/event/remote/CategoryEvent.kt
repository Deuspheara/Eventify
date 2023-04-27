package fr.event.eventify.core.models.event.remote

import fr.event.eventify.R

enum class CategoryEvent(val categoryName: String, val icon: Int) {
    /** Concert */
    CONCERT("Concert", R.drawable.empty_star),

    /** Festival */
    FESTIVAL("Festival", R.drawable.arrow_back),

    /** Sport */
    SPORT("Sports", R.drawable.pingouin),

    /** Theater */
    THEATER("Theater", R.drawable.calendar),

    /** Exhibition */
    EXHIBITION("Exhibition", R.drawable.eventify),

    /** Other */
    OTHER("Others", R.drawable.parameter);

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
                else -> "Others"
            }
        }
    }
}
