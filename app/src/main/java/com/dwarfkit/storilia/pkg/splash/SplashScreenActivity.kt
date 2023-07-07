package com.dwarfkit.storilia.pkg.splash

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dwarfkit.storilia.R
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.databinding.ActivityMainBinding
import com.dwarfkit.storilia.databinding.ActivitySplashScreenBinding
import com.dwarfkit.storilia.pkg.login.LoginActivity
import com.dwarfkit.storilia.pkg.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenViewModel: SplashScreenViewModel by viewModels {
        SplashScreenViewModelFactory.getInstance(
            UserPreferences.getInstance(dataStore)
        )
    }

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            isTokenAvailable()
        }, 2000)
    }

    private fun isTokenAvailable() {
        splashScreenViewModel.getUser().observe(this) { user ->
            if (user.token.isEmpty()) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // Kode yang akan dijalankan setelah jeda waktu
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()


            }
            Log.d("Main", user.token)
        }
    }
}