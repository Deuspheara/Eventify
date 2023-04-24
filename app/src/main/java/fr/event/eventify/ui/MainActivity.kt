package fr.event.eventify.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.event.eventify.R
import fr.event.eventify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    `
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}