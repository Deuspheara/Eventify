package fr.event.eventify.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.data.repository.auth.AuthRepository
import fr.event.eventify.databinding.ActivityMainBinding
import fr.event.eventify.domain.auth.CreateFirebaseUserWithEmailUseCase

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //create firebase user
        var email = "test@test.fr"
        var password = "testtest"


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}