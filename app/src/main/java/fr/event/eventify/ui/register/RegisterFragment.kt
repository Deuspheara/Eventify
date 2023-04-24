package fr.event.eventify.ui.register

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
import fr.event.eventify.databinding.FragmentRegisterBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.btRegister.setOnClickListener {

            viewLifecycleOwner.lifecycle.coroutineScope.launch{
                if(verifyEmail(binding.tfEmail.text.toString())) {
                    if(verifyPasswordEquality(binding.tfPassword.text.toString(), binding.tfConfirmPassword.text.toString())){

                    viewModel.register(
                        binding.tfEmail.text.toString(),
                        binding.tfPassword.text.toString(),
                    )

                    }else{
                        binding.tfConfirmPassword.error = "Passwords are not the same"
                    }
                }else{
                    binding.tfEmail.error = "Email is not valid"
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
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

    private fun verifyEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun verifyPasswordEquality(password: String, passwordConfirmation: String): Boolean {
        return password == passwordConfirmation
    }
}