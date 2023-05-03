package fr.event.eventify.ui.payment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.databinding.FragmentPaymentSummaryBinding
import fr.event.eventify.ui.payment.adapter.PaymentSummaryAdapter

@AndroidEntryPoint
class PaymentSummaryFragment : Fragment() {

    private lateinit var binding: FragmentPaymentSummaryBinding
    private var numberOfParticipant = 1
    private var participantList : List<Participant>? = null
    private val viewModel : PaymentSummaryViewModel by viewModels()
    private var currentEvent : EventLight? = null

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

        binding.apply {
            val price = currentEvent?.ticketPrice ?: 0.00
            val formattedPrice = String.format("%.2f", price)
            val formattedPriceWithoutComma = formattedPrice.replace(",", ".")

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
                                    value = "${formattedPriceWithoutComma}"
                                )
                            )
                        )
                    )
                    createOrderActions.create(order)
                },
                onApprove = OnApprove { approval ->
                    approval.orderActions.capture { captureOrderResult ->
                        Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
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
                    participantNumber = "Participant $i",
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