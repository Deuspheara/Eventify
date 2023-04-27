package fr.event.eventify.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import fr.event.eventify.databinding.FragmentParticipantBinding
import fr.event.eventify.databinding.ItemParticipantBinding

class ParticipantFragment : Fragment() {
    private lateinit var binding: FragmentParticipantBinding
    private val viewModel: PaymentViewModel by viewModels()
    private var numberOfParticipant = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            numberOfParticipant = it.getInt("PARTICIPANT")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPrevious.setOnClickListener {
            findNavController().popBackStack()
        }

        for (i in 1 until numberOfParticipant + 1) {
            val myLayout = ItemParticipantBinding.inflate(layoutInflater, null, false)
            val id = View.generateViewId()
            myLayout.tvNum.text = "Participant $i"
            myLayout.inputEmail.id = id
            binding.linearLayoutParticipant.addView(myLayout.root)
        }
    }

}