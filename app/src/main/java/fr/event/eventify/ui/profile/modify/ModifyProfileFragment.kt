package fr.event.eventify.ui.profile.modify

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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import coil.load
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentModifyProfileBinding
import fr.event.eventify.databinding.FragmentProfileBinding
import fr.event.eventify.ui.profile.ProfileFragmentDirections
import fr.event.eventify.ui.profile.ProfileViewModel
import fr.event.eventify.utils.ImageDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ModifyProfileFragment : Fragment() {

    private val viewModel: ModifyProfileViewModel by activityViewModels()

    private lateinit var binding: FragmentModifyProfileBinding

    private lateinit var startForProfileImageResult: ActivityResultLauncher<Intent>
    private var bitmap: Bitmap? = null
    private var url: String? = null
    private var uuid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModifyProfileBinding.inflate(inflater, container, false)
        binding.btReturnModifyProfile.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { fileUri ->
                    val source = ImageDecoder.createSource(requireContext().contentResolver, fileUri)
                    bitmap = ImageDecoder.decodeBitmap(source)

                    binding.imgModifyProfile.setImageBitmap(bitmap)
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest {state ->
                state.isLoading?.let {

                }
                state.data?.let {
                    binding.apply {
                        tfUsernameModify.setText(it.pseudo)
                        imgModifyProfile.load(it.photoUrl){
                            placeholder(R.drawable.pingouin)
                            error(R.drawable.pingouin)
                        }
                        tfDescriptionModifyProfile.setText(it.phoneNumber)
                        uuid = it.uuid
                        url = it.photoUrl
                    }
                }
                state.error.let {
                    Log.e("ModifyProfileFragment", "Error while collecting user $it")
                }
            }
        }

        //check for upload image
        viewLifecycleOwner.lifecycle.coroutineScope.launch{
            viewModel.upload.collectLatest { state ->
                if (state.error?.isNotEmpty() == true) {
                    state.error.let {
                        Log.e("ModifyProfileFragment", "Error while uploading image $it")
                    }
                }
                state.isLoading.let {

                }
                state.data?.let {
                    Log.d("ModifyProfileFragment", "Image uploaded: $url")
                    url = it

                    uuid?.let { uuid ->
                        url?.let { photoUrl ->
                            viewModel.modifyUser(
                                uuid,
                                binding.tfUsernameModify.text.toString(),
                                binding.tfDescriptionModifyProfile.text.toString(),
                                photoUrl
                            )
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getUser()
        }

        binding.btnValidateProfile.setOnClickListener{
            viewLifecycleOwner.lifecycle.coroutineScope.launch {
                Log.d("ModifyProfileFragment", "Modification of the user")
                if (bitmap == null) {
                    uuid?.let { uuid ->
                        url?.let { photoUrl ->
                            viewModel.modifyUser(
                                uuid,
                                binding.tfUsernameModify.text.toString(),
                                binding.tfDescriptionModifyProfile.text.toString(),
                                photoUrl
                            )
                        }
                    }
                } else {
                    viewModel.uploadPhoto(bitmap!!)
                }
            }
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.imgModifyProfile.setOnClickListener{
            ImageDialog.takePicture(startForProfileImageResult, this.requireActivity())
        }
    }
}