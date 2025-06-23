package com.example.nukarva_florist.ui.home

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.nukarva_florist.R
import com.example.nukarva_florist.databinding.ActivityCoreLayoutBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoreLayoutBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoreLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // Properly set up NavController using NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up navigation controller with bottom navigation
        val navView: BottomNavigationView = binding.bottomNavigationView
        val background = navView
        if (background is MaterialShapeDrawable) {
            background.shadowCompatibilityMode = SHADOW_COMPAT_MODE_ALWAYS
        }
        navView.setupWithNavController(navController)

        // Handle FAB click
//        val fab: FloatingActionButton = binding.fab
//        fab.setOnClickListener {
//            // Handle scan functionality
//            // For example: open scanner fragment or activity
//        }

        // Make sure the navigation menu has enough items before trying to disable one
        if (navView.menu.size() > 2) {
            // Disable the middle item in the bottom navigation (where the FAB is)
            navView.menu.getItem(2).isEnabled = false
        }
    }
}