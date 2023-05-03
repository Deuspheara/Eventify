package fr.event.eventify.ui.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import fr.event.eventify.core.models.payment.local.Participant
import fr.event.eventify.databinding.FragmentPaymentSummaryBinding
import fr.event.eventify.ui.payment.adapter.PaymentSummaryAdapter

class PaymentSummaryFragment : Fragment() {

    private lateinit var binding: FragmentPaymentSummaryBinding
    private var numberOfParticipant = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentSummaryBinding.inflate(inflater, container, false)
        arguments?.let {
            numberOfParticipant = it.getInt("PARTICIPANT")
        }

        binding.apply {
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
                                    value = "1.00"
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
            adapter.participantList.add(Participant(participantNumber = "Participant $i"))
        }
        adapter.notifyDataSetChanged()

    }
}