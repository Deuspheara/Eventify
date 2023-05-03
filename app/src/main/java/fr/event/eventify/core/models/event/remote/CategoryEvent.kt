package fr.event.eventify.core.models.event.remote

import android.os.Parcel
import android.os.Parcelable
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
