package com.example.nukarva_florist.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nukarva_florist.R
import com.example.nukarva_florist.databinding.ActivitySplashBinding
import com.example.nukarva_florist.ui.auth.LoginActivity
import com.example.nukarva_florist.ui.onboarding.OnBoardingActivity
import com.example.nukarva_florist.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()
    private val SPLASH_DELAY = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            statusBarColor = Color.TRANSPARENT
        }

        // Tampilkan animasi Lottie
        binding.lottieAnimationView.setAnimation(R.raw.loading)
        binding.lottieAnimationView.playAnimation()

        observeLoginStatus()
    }

    private fun observeLoginStatus() {
        viewModel.isUserLoggedIn.observe(this) { isLoggedIn ->
            Log.d("SplashActivity", "isUserLoggedIn observed: $isLoggedIn")
            Handler(Looper.getMainLooper()).postDelayed({
                if (isLoggedIn) {
                    // Pengguna sudah login, navigasi ke MainActivity
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    // Pengguna belum login, navigasi ke LoginActivity
                    startActivity(Intent(this, OnBoardingActivity::class.java))
                }
                finish() // Tutup SplashActivity
            }, SPLASH_DELAY)
        }
    }
}
