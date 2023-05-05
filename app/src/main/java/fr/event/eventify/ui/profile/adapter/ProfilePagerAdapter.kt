package fr.event.eventify.ui.profile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import fr.event.eventify.ui.profile.fragment.OrganisedEventFragment
import fr.event.eventify.ui.profile.fragment.ParticipateEventFragment

class ProfilePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val tabTitles = arrayOf("Participé", "Organisé")

    override fun getItemCount(): Int {
        return tabTitles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ParticipateEventFragment()
            else -> OrganisedEventFragment()
        }
    }

    fun getTabTitle(position: Int): CharSequence {
        return tabTitles[position]
    }
}
