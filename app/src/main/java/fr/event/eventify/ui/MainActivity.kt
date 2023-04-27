package fr.event.eventify.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import fr.event.eventify.R
import fr.event.eventify.data.repository.auth.AuthRepository
import fr.event.eventify.databinding.ActivityMainBinding
import fr.event.eventify.domain.auth.CreateFirebaseUserWithEmailUseCase

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val navController : NavController
        get() = (supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment).navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.createEventFragment -> {
                    navController.navigate(R.id.createEventFragment)
                    true
                }
                R.id.connexionFragment -> {
                    navController.navigate(R.id.connexionFragment)
                    true
                }
                else -> false
            }
        }
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}