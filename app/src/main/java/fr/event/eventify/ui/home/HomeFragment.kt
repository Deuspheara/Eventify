package fr.event.eventify.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.core.models.event.remote.CategoryEvent
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.core.models.event.remote.FilterEvent
import fr.event.eventify.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar


@AndroidEntryPoint
class HomeFragment : Fragment() {
   private lateinit var binding: FragmentHomeBinding
   private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var pagingAdapter: EventPagingAdapter
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

        pagingAdapter = EventPagingAdapter()

        binding.rvEvents.adapter = pagingAdapter
        binding.rvEvents.layoutManager =  LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            try {
                viewModel.eventsPaginated.collectLatest { state ->
                     if (state.error.isNotEmpty()) {
                        state.error.let {
                            Log.e("HomeFragment", "Error while collecting events paginated $it" )
                        }
                    }
                    state.isLoading.let {
                        Log.d("HomeFragment", "isLoading: $it")
                    }
                    state.data?.let {
                        it.map {
                            Log.d("HomeFragment", "event: ${it.name}")
                        }
                        pagingAdapter.submitData(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error while collecting events paginated", e)
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getEventsPaginated(FilterEvent.NAME,CategoryEvent.FESTIVAL)
        }

//        viewLifecycleOwner.lifecycle.coroutineScope.launch {
//            try {
//                viewModel.eventList.collectLatest { state ->
//                    if (state.error.isNotEmpty()) {
//                        state.error.let {
//                            Log.e("HomeFragment", "Error while collecting events paginated $it" )
//                        }
//                    }
//                    state.isLoading.let {
//                        Log.d("HomeFragment", "isLoading: $it")
//                    }
//                    state.data?.let {
//                        Log.d("HomeFragment", "Event list: ${it}")
//                        it.map {
//                            Log.d("HomeFragment", "event: ${it.name}")
//                        }
//                    }
//                }
//            }catch (e: Exception){
//                Log.e("HomeFragment", "Error while collecting events paginated", e)
//            }
//
//        }
//
//        viewLifecycleOwner.lifecycle.coroutineScope.launch {
//            viewModel.getEvents(0, 20, FilterEvent.NAME, CategoryEvent.FESTIVAL)
//        }




        viewLifecycleOwner.lifecycle.coroutineScope.launch{
            viewModel.connected.collectLatest { state ->
                state.error.let {message ->
                    //Log.d("HomeFragment", "error: $message")
                    isConnect = false
                }
                state.data?.let {
                    isConnect = it
                }
            }

            viewModel.isConnected()
        }


//        viewLifecycleOwner.lifecycle.coroutineScope.launch {
//            Log.d("HomeFragment", "onCreateView: $isConnect")
//            //if(isConnect){
//                Log.d("HomeFragment", "createEvent, isConnect: $isConnect")
//
//                viewModel.createEvent(
//                    event= Event(
//                        name = "test",
//                        author = "test",
//                        description = "test",
//                        date = Timestamp(Date(Calendar.getInstance().timeInMillis)),
//                        location = Event.LocationEvent(
//                            id = "test",
//                            name = "test",
//                            latitude = 0.0,
//                            longitude = 0.0,
//                            address = "test",
//                            city = "test",
//                            country = "test",
//                            zipCode = "test"
//                        ),
//                        image = "test",
//                        nbTickets = 0,
//                        participants = listOf(),
//                        categoryEvent = CategoryEvent.CONCERT,
//                        ticketPrice = Event.PriceEvent(
//                            amount = 0.0,
//                            currency = "test"
//                        )
//                    )
//                )
//           // }
//
//
//            viewModel.event.collectLatest { state ->
//                state.error?.let {
//
//                }
//                state.isLoading?.let {
//
//                }
//                state.data?.let {
//                    Log.d("HomeFragment", "onCreateView: $it")
//                }
//
//            }
//        }
    }


}