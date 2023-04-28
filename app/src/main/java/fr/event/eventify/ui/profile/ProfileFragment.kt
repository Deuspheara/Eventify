package fr.event.eventify.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
   private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
    ): View {
         binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.btReturnProfile.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
         return binding.root
    }
}