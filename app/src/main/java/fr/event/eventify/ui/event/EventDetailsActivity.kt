package fr.event.eventify.ui.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.os.bundleOf
import coil.load
import fr.event.eventify.R
import fr.event.eventify.databinding.ActivityEventDetailsBinding
import fr.event.eventify.ui.payment.PaymentActivity

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailsBinding
    private var filled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)

        binding.buttonParticipate.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        binding.buttonFav.setOnClickListener { it as ImageButton
            it.setImageResource(if (filled) R.drawable.empty_star else R.drawable.filled_star)
            filled = !filled
        }
        binding.apply {
            val bundle = intent.extras
            val ticketPriceBundle = bundle?.getBundle("ticket_price")
            val date = intent.getStringExtra("date")
            val participantsArray = intent.getStringArrayExtra("participants") as? Array<String>
            val numberOfParticipants = participantsArray?.size ?: 0
            tvName.text = intent.getStringExtra("name")
            tvPrice.text = ticketPriceBundle?.getDouble("amount").toString() + "€"
            tvPlace.text = bundle?.getBundle("location")?.getString("name")
            tvDate.text = date?.substring(0, date.indexOf("T"))
            tvDescription.text = intent.getStringExtra("description")
            tvParticipate.text = numberOfParticipants.toString() + " participants"
            ivPreview.load(intent.getStringExtra("image")){
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