package fr.event.eventify.ui.profile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentMyEventBinding
import fr.event.eventify.ui.profile.MyEventViewModel

class MyEventFragment : Fragment() {
    private lateinit var binding: FragmentMyEventBinding
    private val viewModel : MyEventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyEventFragment()
    }
}