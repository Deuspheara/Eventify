package fr.event.eventify.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar


@AndroidEntryPoint
class HomeFragment : Fragment() {
   private lateinit var binding: FragmentHomeBinding
   private val viewModel: HomeViewModel by viewModels()
    private var isConnect = false
    override fun onCreateView(
         inflater: LayoutInflater, container: ViewGroup?,
         savedInstanceState: Bundle?
    ): View {
         binding = FragmentHomeBinding.inflate(inflater, container, false)


         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.coroutineScope.launch{
            viewModel.connected.collectLatest { state ->
                state.error.let {message ->
                    Log.d("HomeFragment", "error: $message")
                    isConnect = false
                }
                state.data?.let {
                    isConnect = it
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.isConnected()
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            Log.d("HomeFragment", "onCreateView: $isConnect")
            //if(isConnect){
                Log.d("HomeFragment", "createEvent, isConnect: $isConnect")
                val calendar: Calendar = Calendar.getInstance()
                calendar.set(2023, Calendar.APRIL, 26, 15, 30, 0)
                val specificDate: Date = calendar.time
                viewModel.createEvent(
                    event= Event(
                        id = "test",
                        name = "test",
                        author = "test",
                        description = "test",
                        date = specificDate,
                        location = Event.LocationEvent(
                            id = "test",
                            name = "test",
                            latitude = 0.0,
                            longitude = 0.0,
                            address = "test",
                            city = "test",
                            country = "test",
                            zipCode = "test"
                        ),
                        image = "test",
                        nbTickets = 0,
                        participants = listOf(),
                        categoryEvent = CategoryEvent.CONCERT,
                        ticketPrice = Event.PriceEvent(
                            amount = 0.0,
                            currency = "test"
                        )
                    )
                )
           // }


            viewModel.event.collectLatest { state ->
                state.error?.let {

                }
                state.isLoading?.let {

                }
                state.data?.let {
                    Log.d("HomeFragment", "onCreateView: $it")
                }

            }
        }
    }


}