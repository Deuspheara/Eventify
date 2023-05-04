package fr.event.eventify.ui.create_event

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
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
import fr.event.eventify.R
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.databinding.FragmentCreateEventBinding
import fr.event.eventify.utils.ImageDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreateEventFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentCreateEventBinding
    private val viewModel: CreateEventViewModel by viewModels()
    private var cal = Calendar.getInstance()
    private val categoryEvents = CategoryEvent.values().toList()
    private lateinit var startForEventImageResult: ActivityResultLauncher<Intent>
    private var url: String? = null
    private var currentUser: RemoteUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateEventBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgCreateEvent.setOnClickListener {
            ImageDialog.takePicture(
                startForEventImageResult,
                this.requireActivity(),
                640,
                0.5f,
                16f,
                9f
            )
        }
        //check for upload image
        viewLifecycleOwner.lifecycle.coroutineScope.launch{
            viewModel.upload.collectLatest { state ->
                if (state.error?.isNotEmpty() == true) {
                    state.error.let {
                        Log.e("CreateEventFragment", "Error while uploading image $it")
                    }
                }
                state.isLoading.let {

                }
                state.data?.let {it ->
                    Log.d("CreateEventFragment", "Image uploaded: $url")
                    url = it
                }
            }
        }



        startForEventImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { fileUri ->
                        val source =
                            ImageDecoder.createSource(requireContext().contentResolver, fileUri)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        Log.d("CreateEventFragment", "Image selected")
                        viewLifecycleOwner.lifecycle.coroutineScope.launch {
                            viewModel.upload.collectLatest { state ->
                                if (state.error?.isNotEmpty() == true) {
                                    state.error.let {
                                        Log.e(
                                            "CreateEventFragment",
                                            "Error while uploading image $it"
                                        )
                                    }
                                }
                                state.isLoading.let {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e("CreateEventFragment", "Error while getting user $it")
                    }
                }
                state.isLoading.let {

                }
                state.data?.let { user ->
                    currentUser = user
                    Log.d("CreateEventFragment", "User: $user")
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch{
            viewModel.getUser()
        }

        binding.imgCreateEvent.setOnClickListener{
            ImageDialog.takePicture(startForEventImageResult, requireActivity(),640,0.5f,16f,9f)
        }

                        }
                        state.data?.let {
                            Log.d("CreateEventFragment", "Image uploaded: $it")
                        }
                    }
                }
                viewLifecycleOwner.lifecycle.coroutineScope.launch {
                    viewModel.uploadPhoto(bitmap)
                }


                binding.imgCreateEvent.setImageBitmap(bitmap)
            }
        }
    }
        binding.btCreateEvent.setOnClickListener {
            binding.apply {
                val checkPriceNotNull = tfPriceEvent.text.toString().isNotEmpty()
                val checkNbTicketsNotNull = tfPlacesEvent.text.toString().isNotEmpty()
                if(tfNameEvent.text.toString().isEmpty()) {
                    tfNameEvent.error = "Please enter a name"
                    tfNameEvent.requestFocus()
                    return@setOnClickListener
                }
                //price format to 2.00
                val price = if (checkPriceNotNull) "%.2f".format(tfPriceEvent.text.toString().toDouble()) else "0.00"
                val priceWithoutComma = price.replace(",",".")
                //log user
                Log.d("CreateEventFragment", "current user: $currentUser")
                viewModel.createEvent(
                    Event(
                        name = tfNameEvent.text.toString(),
                        author = currentUser?.uuid ?: "",
                        description = tfDescriptionEvent.text.toString(),
                        date = Timestamp(Calendar.getInstance().time),
                        location = Event.LocationEvent(
                            name = tfLocationEvent.text.toString(),
                        ),
                        image = url,
                        ticketPrice = Event.PriceEvent(
                            currency = "EUR",
                            amount = priceWithoutComma.toDouble(),
                        ),
                        nbTickets = if (checkNbTicketsNotNull) tfPlacesEvent.text.toString().toInt() else 0,
                        categoryEvent = CategoryEvent.FESTIVAL,
                    )
                )
            }
        }


        binding.tfDateEvent.setOnClickListener {
            DatePickerDialog(
                this.requireContext(), R.style.datepicker, this, cal.get(Calendar.YEAR), cal.get(
                    Calendar.MONTH
                ), cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val adapter = CategorySpinnerAdapter(this.requireContext())
        binding.spCategory.adapter = adapter

    }

    private fun updateDateInView() {
        binding.tfDateEvent.setText(
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(cal.time)
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView()
    }
}