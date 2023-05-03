package fr.event.eventify.core.models.event.remote

import fr.event.eventify.R

enum class CategoryEvent(val categoryName: String, val icon: Int) {
    /** Concert */
    CONCERT("Concert", R.drawable.ic_category_concert),

    /** Festival */
    FESTIVAL("Festival", R.drawable.ic_category_festival),

    /** Sport */
    SPORT("Sports", R.drawable.ic_category_sports),

    /** Theater */
    THEATER("Theater", R.drawable.ic_category_theater),

    /** Exhibition */
    EXHIBITION("Exhibition", R.drawable.ic_category_exhib),

    /** Other */
    OTHER("Others", R.drawable.ic_category_others);

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
