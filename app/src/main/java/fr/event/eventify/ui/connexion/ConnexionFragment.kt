package fr.event.eventify.ui.connexion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentConnexionBinding


class ConnexionFragment : Fragment() {

    private lateinit var binding: FragmentConnexionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnexionBinding.inflate(inflater, container, false)
        return binding.root
    }

}