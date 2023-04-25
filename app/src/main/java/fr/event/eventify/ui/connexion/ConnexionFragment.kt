package fr.event.eventify.ui.connexion

import android.app.Activity
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
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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

    private val TAG = "ConnexionFragment"

    private lateinit var binding: FragmentConnexionBinding
    private val viewModel: ConnexionViewModel by viewModels()
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
        binding = FragmentConnexionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btCreateAccount.setOnClickListener {
            val action = ConnexionFragmentDirections.actionConnexionFragmentToRegisterFragment()
            view.findNavController().navigate(action)
        }

        //google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btGoogleConnect2.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

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