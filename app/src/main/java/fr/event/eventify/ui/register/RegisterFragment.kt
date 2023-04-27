package fr.event.eventify.ui.register

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.databinding.FragmentRegisterBinding
import fr.event.eventify.utils.ImageDialog
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

    private lateinit var startForProfileImageResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)

        startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { fileUri ->
                    val source = ImageDecoder.createSource(requireContext().contentResolver, fileUri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    binding.imgProfile.setImageBitmap(bitmap)
                }
            }
        }

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

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest { state ->
                state.isLoading.let {
                    //Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                }
                state.error.let {
                    //Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
                state.data?.let {
                    Log.d(TAG, "onCreateView: ${it.uid}")
                    viewModel.registerOnFireStore(
                        RemoteUser(
                            uuid = it.uid,
                            displayName = binding.tfName.text.toString(),
                            pseudo = "",
                            email = it.email.toString(),
                            phoneNumber = binding.tfPhoneNumber.text.toString(),
                            photoUrl = "",
                            providerID = it.providerId,
                            isEmailVerified = it.isEmailVerified
                        )
                    )
                    Toast.makeText(context, "Success user created", Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.remoteUser.collectLatest { state ->
                state.isLoading.let {
                    Log.d(TAG, "remoteUser: Loading")
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                }
                state.error.let {
                    Log.d(TAG, "remoteUser: Error")
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
                state.data?.let {
                    Log.d(TAG, "remoteUser: Success")
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
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

        binding.imgProfile.setOnClickListener{
            ImageDialog.takePicture(startForProfileImageResult, this.requireActivity())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun verifyEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun verifyPasswordEquality(password: String, passwordConfirmation: String): Boolean {
        return password == passwordConfirmation
    }
}