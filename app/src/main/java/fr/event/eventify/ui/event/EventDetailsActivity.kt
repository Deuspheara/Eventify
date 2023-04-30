package fr.event.eventify.ui.event

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.os.bundleOf
import coil.load
import fr.event.eventify.R
import fr.event.eventify.core.models.event.local.EventLight
import fr.event.eventify.core.models.event.remote.Event
import fr.event.eventify.databinding.ActivityEventDetailsBinding
import fr.event.eventify.ui.payment.PaymentActivity

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailsBinding
    private var filled = false
    private var event: EventLight? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)

        binding.buttonParticipate.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("event", event)
            startActivity(intent)
        }

        binding.buttonFav.setOnClickListener { it as ImageButton
            it.setImageResource(if (filled) R.drawable.empty_star else R.drawable.filled_star)
            filled = !filled
        }
        binding.apply {

            event = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("event", EventLight::class.java)
            } else {
                intent.getParcelableExtra<EventLight>("event")
            }
            tvName.text = event?.name
            tvPrice.text = event?.ticketPrice?.toString() + "€"
            tvPlace.text = event?.location.toString()
            tvDate.text = event?.date.toString()
            tvDescription.text = event?.description
            tvParticipate.text = event?.nbParticipants.toString() + " participants"
            ivPreview.load(event?.image){
                placeholder(R.drawable.app_icon)
                error(R.drawable.app_icon)
            }
            buttonBack.setOnClickListener {
                finish()
            }
        }
        setContentView(binding.root)
    }
}