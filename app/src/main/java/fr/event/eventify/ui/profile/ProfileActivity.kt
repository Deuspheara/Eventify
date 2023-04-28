package fr.event.eventify.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.event.eventify.R
import fr.event.eventify.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        
        setContentView(binding.root)
    }
}