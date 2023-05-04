package fr.event.eventify.ui.profile_detail.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentOrganisedEventDetailBinding
import fr.event.eventify.ui.profile_detail.adapter.OrganisedEventDetailAdapter

class OrganisedEventDetailFragment : Fragment() {
   private lateinit var binding: FragmentOrganisedEventDetailBinding
   private lateinit var adapter: OrganisedEventDetailAdapter
   private val viewModel: OrganisedEventDetailViewModel by viewModels()

    override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
    ): View {
          binding = FragmentOrganisedEventDetailBinding.inflate(inflater, container, false)
          return binding.root
    }
}