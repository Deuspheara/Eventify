package fr.event.eventify.ui.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
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

        setContentView(binding.root)
    }
}