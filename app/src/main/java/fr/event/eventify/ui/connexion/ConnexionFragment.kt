package fr.event.eventify.ui.connexion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentConnexionBinding
import fr.event.eventify.ui.register.AuthState
import fr.event.eventify.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ConnexionFragment : Fragment() {

    private lateinit var binding: FragmentConnexionBinding
    private val viewModel: ConnexionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnexionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            binding.apply {
                btConnex.setOnClickListener {
                    val email = tfEmail2.text.toString()
                    val password = tfPassword2.text.toString()
                    if (email.isEmpty() || password.isEmpty()) {
                        tfEmail2.error = "Email is required"
                        tfPassword2.error = "Password is required"
                    } else {
                        viewModel.signIn(email, password)
                    }
                }

                viewModel.user.collectLatest { state ->
                    state.isLoading.let {
                        //Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                    }
                    state.error.let {
                        //Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }
                    state.data?.let {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



    }

}