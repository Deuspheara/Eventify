package fr.event.eventify.ui.create_event

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import fr.event.eventify.R
import fr.event.eventify.core.models.event.remote.CategoryEvent
import java.util.Locale

class CategorySpinnerAdapter(context: Context) : ArrayAdapter<CategoryEvent>(context, 0, CategoryEvent.values()) {

    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_spinner_category, parent, false)
        } else {
            view = convertView
        }
        getItem(position)?.let { category ->
            setItemForCountry(view, category)
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View

        if (position == 0) {
            view = layoutInflater.inflate(R.layout.item_spinner_category, parent, false)
            view.setOnClickListener {
                val root = parent.rootView
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
            }
        } else {
            view = layoutInflater.inflate(R.layout.item_spinner_category, parent, false)
            getItem(position)?.let { country ->
                setItemForCountry(view, country)
            }
        }
        return view
    }

    override fun getItem(position: Int): CategoryEvent? {
        if (position == 0) {
            return null
        }
        return super.getItem(position - 1)
    }

    override fun getCount() = super.getCount() + 1
    override fun isEnabled(position: Int) = position != 0

    private fun setItemForCountry(view: View, category: CategoryEvent) {
        val tvCountry = view.findViewById<TextView>(R.id.tv_category)
        val ivCountry = view.findViewById<ImageView>(R.id.iv_category)
        val countryName = Locale("", category.categoryName).displayCountry
        tvCountry.text = countryName
        ivCountry.setBackgroundResource(category.icon)
    }
}