package fr.event.eventify.ui.payment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.core.models.auth.remote.RemoteUser
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.core.models.payment.remote.Transaction
import fr.event.eventify.core.models.payment.remote.TransactionType
import fr.event.eventify.databinding.FragmentPaymentSummaryBinding
import fr.event.eventify.ui.payment.adapter.PaymentSummaryAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentSummaryFragment : Fragment() {

    private lateinit var binding: FragmentPaymentSummaryBinding
    private var numberOfParticipant = 1
    private var participantList : List<Participant>? = null
    private val viewModel : PaymentSummaryViewModel by viewModels()
    private var currentEvent : EventLight? = null
    private var currentUser : RemoteUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentSummaryBinding.inflate(inflater, container, false)
        arguments?.let {
            numberOfParticipant = it.getInt("PARTICIPANT")
        }
        participantList =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("participants", Participant::class.java)
        } else {
            arguments?.getParcelableArrayList("participants")
        }

        currentEvent = viewModel.getCurrentEvent()

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.event.collectLatest { state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e("PaymentSummaryFragment", "Error while getting event $it")
                    }
                }
                state.isLoading.let {

                }
                state.data?.let { event ->
                    Log.d("PaymentSummaryFragment", "Success adding participant to event")
                    findNavController().navigateUp()
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collectLatest {state ->
                if (state.error.isNotEmpty()) {
                    state.error.let {
                        Log.e("PaymentSummaryFragment", "Error while getting user $it")
                    }
                }
                state.isLoading.let {

                }
                state.data?.let { user ->
                    currentUser = user
                }

            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.transaction.collectLatest { state ->
                if (state.error?.isNotEmpty() == true) {
                    state.error.let {
                        Log.e("PaymentSummaryFragment", "Error while adding transaction $it")
                    }
                }
                state.isLoading.let {

                }
                state.data?.let { transaction ->
                    Toast.makeText(requireContext(), "Transaction added", Toast.LENGTH_SHORT).show()
                }

            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.getUser()
        }

        binding.apply {
            val price = (currentEvent?.ticketPrice?.times(numberOfParticipant))
            val formattedPrice = String.format("%.2f", price)
            val formattedPriceWithoutComma = formattedPrice.replace(",", ".")
            imgHeaderTicketInformation.load(currentEvent?.image){
                placeholder(R.drawable.logo_gradient)
                error(R.drawable.logo_gradient)
            }

            paymentButtonContainer.setup(
                createOrder = CreateOrder { createOrderActions ->
                    val order = Order(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(
                            userAction = UserAction.PAY_NOW
                        ),
                        purchaseUnitList = listOf(
                            PurchaseUnit(
                                amount = Amount(
                                    currencyCode = CurrencyCode.EUR,
                                    value = formattedPriceWithoutComma
                                )
                            )
                        )
                    )
                    createOrderActions.create(order)
                },
                onApprove = OnApprove { approval ->
                    approval.orderActions.capture { captureOrderResult ->
                        Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                        viewLifecycleOwner.lifecycle.coroutineScope.launch {
                            viewModel.getCurrentEvent()?.id?.let { eventId ->
                                Log.d("PaymentSummaryFragment", "EventId: $eventId")
                                viewModel.addParticipantToEvent(eventId, participantList ?: listOf())
                            }
                        }
                        viewLifecycleOwner.lifecycle.coroutineScope.launch {
                            viewModel.addTransaction(
                                Transaction(
                                    eventId = currentEvent?.id ?: "",
                                    amount = formattedPriceWithoutComma.toDouble(),
                                    currency = "EUR",
                                    transactionType = TransactionType.PAYMENT,
                                    receiver = currentEvent?.author ?: "",
                                    user = currentUser?.uuid ?: "",
                                    participants = participantList ?: listOf()
                                )
                            )
                        }
                    }
                },
                onError = OnError { errorInfo ->
                    Log.d("OnError", "Error: $errorInfo")
                }
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPrevious.setOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = PaymentSummaryAdapter()
        binding.rvParticipant.adapter = adapter
        binding.rvParticipant.layoutManager = LinearLayoutManager(requireContext())
        for (i in 1 until numberOfParticipant + 1) {
            adapter.participantList.add(
                Participant(
                    firstName = participantList?.get(i-1)?.firstName ?: "",
                    lastName = participantList?.get(i-1)?.lastName ?: "",
                    email = participantList?.get(i-1)?.email ?: "",
                )
            )
            Log.d("Participant", "Participant[$i] : ${participantList?.get(i-1)?.firstName}")
        }

        binding.tvTotalTicket.text = "${numberOfParticipant.times(currentEvent?.ticketPrice ?: 0.0)} €"
        adapter.notifyDataSetChanged()

    }
}