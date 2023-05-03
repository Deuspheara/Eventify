package fr.event.eventify.ui.profile.modify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentModifyProfileBinding
import fr.event.eventify.databinding.FragmentProfileBinding

class ModifyProfileFragment : Fragment() {
    private lateinit var binding: FragmentModifyProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModifyProfileBinding.inflate(inflater, container, false)
        binding.btReturnModifyProfile.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}