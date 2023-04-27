package fr.event.eventify.core.models.event.remote

import fr.event.eventify.R

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
        fun toString(category: CategoryEvent): String {

            return when (category) {
                CONCERT -> "concert"
                FESTIVAL -> "festival"
                SPORT -> "sport"
                THEATER -> "theater"
                EXHIBITION -> "exhibition"
                else -> "other"
            }
        }

        //return R.drawable.ic_music
        fun toIcon(categoryEvent: CategoryEvent): Int {
            return when (categoryEvent) {
                CONCERT -> R.drawable.calendar
                FESTIVAL -> R.drawable.eventify
                SPORT -> R.drawable.eventify
                THEATER -> R.drawable.eventify
                EXHIBITION -> R.drawable.eventify
                else -> R.drawable.eventify
            }
        }
    }
}
val CategoryEvent.stringValue: String
    get() = CategoryEvent.toString(this)
val CategoryEvent.iconValue : Int
    get() = CategoryEvent.toIcon(this)
