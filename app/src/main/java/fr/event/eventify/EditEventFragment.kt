package fr.event.eventify

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.databinding.FragmentCreateEventBinding
import fr.event.eventify.databinding.FragmentEditEventBinding
import fr.event.eventify.ui.create_event.CategorySpinnerAdapter
import fr.event.eventify.utils.ImageDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class EditEventFragment : Fragment() {

    private lateinit var binding: FragmentEditEventBinding

    private lateinit var startForEventImageResult: ActivityResultLauncher<Intent>
    private var url: String? = null
    private var bitmap: Bitmap? = null
    private var cal = Calendar.getInstance()
    private var selectedDate: Timestamp? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditEventBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgEditEvent.setOnClickListener {
            ImageDialog.takePicture(
                startForEventImageResult,
                this.requireActivity(),
                640,
                0.5f,
                16f,
                9f
            )
        }

        startForEventImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { fileUri ->
                        val source =
                            ImageDecoder.createSource(requireContext().contentResolver, fileUri)
                        bitmap = ImageDecoder.decodeBitmap(source)
                        Log.d("EditEventFragment", "Image selected")

                        binding.imgEditEvent.setImageBitmap(bitmap)
                    }
                }
            }

        binding.tfDateEditEvent.setOnClickListener {
            // Create a new DatePickerDialog
            val datePicker = DatePickerDialog(
                requireContext(),
                R.style.datepicker,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    // Set the date values in the Calendar instance
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Create a new TimePickerDialog
                    val timePicker = TimePickerDialog(
                        requireContext(),
                        R.style.timepicker,
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            // Set the time values in the Calendar instance
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            cal.set(Calendar.MINUTE, minute)

                            // Update the date and time in the UI
                            updateDateTimeInView()

                            // Store the selected date and time as a Timestamp object
                            selectedDate = Timestamp(cal.time)
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    )
                    // Show the TimePickerDialog
                    timePicker.show()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            // Show the DatePickerDialog
            datePicker.show()
        }

        val adapter = CategorySpinnerAdapter(this.requireContext())
        binding.spCategory.adapter = adapter
    }

    private fun updateDateTimeInView() {
        binding.tfDateEditEvent.setText(
            SimpleDateFormat(
                "dd MMM yyyy HH:mm",
                Locale.getDefault()
            ).format(cal.time)
        )
    }


}