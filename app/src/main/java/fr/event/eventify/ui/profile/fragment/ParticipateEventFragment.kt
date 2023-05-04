package fr.event.eventify.ui.profile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentParticipateEventBinding

class ParticipateEventFragment : Fragment() {
   private lateinit var binding: FragmentParticipateEventBinding

    override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
    ): View {
         binding = FragmentParticipateEventBinding.inflate(inflater, container, false)
         return binding.root
    }
}