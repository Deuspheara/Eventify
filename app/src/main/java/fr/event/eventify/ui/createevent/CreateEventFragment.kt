package fr.event.eventify.ui.createevent

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import fr.event.eventify.R
import fr.event.eventify.databinding.FragmentCreateEventBinding
import fr.event.eventify.utils.ImageDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CreateEventFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentCreateEventBinding
    private var cal = Calendar.getInstance()

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
            ImageDialog.takePicture(startForEventImageResult, this.requireActivity(),640,0.5f,16f,9f)
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

        binding.tfDateEvent.setOnClickListener{
            DatePickerDialog(this.requireContext(), R.style.datepicker, this, cal.get(Calendar.YEAR), cal.get(
                Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun updateDateInView() {
        binding.tfDateEvent.setText(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time))
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView()
    }


}