package fr.event.eventify.ui.createevent

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import fr.event.eventify.databinding.FragmentCreateEventBinding
import fr.event.eventify.utils.ImageDialog


class CreateEventFragment : Fragment() {

    private lateinit var binding: FragmentCreateEventBinding

    private lateinit var startForEventImageResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateEventBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgCreateEvent.setOnClickListener{
            ImageDialog.takePicture(startForEventImageResult, this.requireActivity())
        }

        startForEventImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { fileUri ->
                    val source = ImageDecoder.createSource(requireContext().contentResolver, fileUri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    binding.imgCreateEvent.setImageBitmap(bitmap)
                }
            }
        }

    }


}