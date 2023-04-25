package fr.event.eventify.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentRegisterBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val TAG = "RegisterFragment"

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    viewModel.loginWithGoogle(account?.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign-in failed", e)
                }
            }
        }

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

        //google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btGoogleConnect.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
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