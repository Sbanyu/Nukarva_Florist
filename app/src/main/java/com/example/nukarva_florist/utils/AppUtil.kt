package com.example.nukarva_florist.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nukarva_florist.R
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Locale

object AppUtil {

    fun setStatusBarBasedOnBackground(activity: Activity, backgroundColor: Int) {
        val window = activity.window

        // Deteksi apakah warna background terang atau gelap
        val isLightBackground = isColorLight(backgroundColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ way
            window.setDecorFitsSystemWindows(false)

            // Atur warna ikon status bar
            val controller = window.insetsController
            if (isLightBackground) {
                // Jika background terang (putih), ikon status bar gelap (hitam)
                controller?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                // Jika background gelap, ikon status bar terang (putih)
                controller?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        } else {
            // Legacy way (Android 6.0 - 10)
            if (isLightBackground) {
                // Jika background terang (putih), ikon status bar gelap (hitam)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                // Jika background gelap, ikon status bar terang (putih)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }

        // Atur warna status bar menjadi transparent agar menyesuaikan dengan background
        window.statusBarColor = Color.TRANSPARENT

        // Add padding to your root layout equal to the status bar height
        val rootView = activity.findViewById<View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(0, statusBarHeight, 0, 0)
            insets
        }
    }

    // Fungsi untuk menentukan apakah warna termasuk terang atau gelap
    fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5  // Jika darkness < 0.5, warna termasuk terang
    }

    fun setupPasswordVisibilityToggle(context: Context, til: TextInputLayout, et: EditText) {
        // Set transformation method dari awal (untuk dot yang lebih besar)
        et.transformationMethod = BiggerDotPasswordTransformationMethod()
        et.typeface = ResourcesCompat.getFont(context, R.font.urbanist_medium)

        // Variable untuk tracking state password visibility
        var isPasswordVisible = false

        til.setEndIconOnClickListener {
            // Toggle visibility state
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // Menampilkan password dengan langsung menghapus transformation method
                et.transformationMethod = null
                til.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_on)
            } else {
                // Menyembunyikan password dengan menggunakan kembali BiggerDotPasswordTransformationMethod
                et.transformationMethod = BiggerDotPasswordTransformationMethod()
                til.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_off)
            }

            // Reapply typeface untuk menjaga konsistensi font
            et.typeface = ResourcesCompat.getFont(context, R.font.urbanist_medium)

            // Reset posisi kursor
            et.setSelection(et.text?.length ?: 0)
        }
    }

    fun animateHeartFill(heartView: View) {
        // Scale up animation
        val scaleUpX = ObjectAnimator.ofFloat(heartView, "scaleX", 1f, 1.3f)
        val scaleUpY = ObjectAnimator.ofFloat(heartView, "scaleY", 1f, 1.3f)

        // Scale down animation
        val scaleDownX = ObjectAnimator.ofFloat(heartView, "scaleX", 1.3f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(heartView, "scaleY", 1.3f, 1f)

        // Rotation animation for extra effect
        val rotation = ObjectAnimator.ofFloat(heartView, "rotation", 0f, 15f, -15f, 0f)

        val scaleUpAnimator = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
            duration = 150
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleDownAnimator = AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY, rotation)
            duration = 200
            interpolator = BounceInterpolator()
        }

        val finalAnimator = AnimatorSet().apply {
            playSequentially(scaleUpAnimator, scaleDownAnimator)
        }

        finalAnimator.start()
    }

    fun animateHeartUnfill(view: View) {
        // Simple scale animation for unfavorite
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f, 1f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.7f, 1f)

        val animatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }

        animatorSet.start()
    }

    fun Double.toRupiahWithoutDecimal(): String {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        formatter.maximumFractionDigits = 0
        return formatter.format(this).replace("Rp", "Rp ")
    }

}