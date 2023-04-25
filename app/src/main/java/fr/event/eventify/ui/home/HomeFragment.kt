package fr.event.eventify.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
   private lateinit var binding: FragmentHomeBinding
   private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
         inflater: LayoutInflater, container: ViewGroup?,
         savedInstanceState: Bundle?
    ): View {
         binding = FragmentHomeBinding.inflate(inflater, container, false)
         return binding.root
    }


}