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
                    }
                }
                state.error.let {
                    Log.e("ProfileFragment", "Error while collecting user $it")
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getUser()
        }

        binding.btnValidateProfile.setOnClickListener{
            uuid?.let { it1 -> viewModel.modifyUser(it1,binding.tfUsernameModify.text.toString(),binding.tfDescriptionModifyProfile.text.toString(),"") }
        }

        binding.imgModifyProfile.setOnClickListener{
            ImageDialog.takePicture(startForProfileImageResult, this.requireActivity())
        }
    }
}