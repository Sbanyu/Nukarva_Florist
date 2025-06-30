package com.example.nukarva_florist.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.AppPreferences
import com.example.nukarva_florist.data.req.AuthRequest
import com.example.nukarva_florist.data.req.OtpRequest
import com.example.nukarva_florist.databinding.ActivityOtpBinding
import com.example.nukarva_florist.ui.home.CoreActivity
import com.example.nukarva_florist.utils.AppUtil
import com.example.nukarva_florist.utils.DialogUtils.createLoadingDialog
import com.example.nukarva_florist.utils.DialogUtils.showCustomStatusDialog
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.OtpViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtpActivity : AppCompatActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private lateinit var binding: ActivityOtpBinding
    private val otpDigits = mutableListOf<Char>()
    private val maxOtpLength = 4
    private var previousOtpSize = 0
    private var otpTimer: CountDownTimer? = null
    private val viewModel: OtpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreferences = AppPreferences(this)
        val rootView = findViewById<View>(R.id.root_layout)
        val backgroundColor = (rootView.background as? ColorDrawable)?.color ?: Color.WHITE
        AppUtil.setStatusBarBasedOnBackground(this, backgroundColor)

        binding.emailText.text = appPreferences.getEmail()
        viewModel.sendOtp()
        sendOtp()
        setListenerKeypad()
        setupOtpInput()
        setupOtpTimer()
    }

    private fun setListenerKeypad() {
        try {
            val keypad1 = binding.keypad1
            val keypad2 = binding.keypad2
            val keypad3 = binding.keypad3
            val keypad4 = binding.keypad4
            val keypad5 = binding.keypad5
            val keypad6 = binding.keypad6
            val keypad7 = binding.keypad7
            val keypad8 = binding.keypad8
            val keypad9 = binding.keypad9
            val keypad0 = binding.keypad0
            val keypadBackspace = binding.keypadBackspace
            val btnReset = binding.resetButton

            Log.d(TAG, "setListenerKeypad: Setting up listeners")

            keypad1.setOnClickListener { onNumberPressed('1') }
            keypad2.setOnClickListener { onNumberPressed('2') }
            keypad3.setOnClickListener { onNumberPressed('3') }
            keypad4.setOnClickListener { onNumberPressed('4') }
            keypad5.setOnClickListener { onNumberPressed('5') }
            keypad6.setOnClickListener { onNumberPressed('6') }
            keypad7.setOnClickListener { onNumberPressed('7') }
            keypad8.setOnClickListener { onNumberPressed('8') }
            keypad9.setOnClickListener { onNumberPressed('9') }
            keypad0.setOnClickListener { onNumberPressed('0') }

            keypadBackspace.setOnClickListener { onBackspacePressed() }

            btnReset.setOnClickListener {
                otpDigits.clear()
                previousOtpSize = 0
                updateOtpViews()
            }
        }catch (e: Exception) {
            Log.e(TAG, "Error setting up keypad listeners", e)
        }
    }

    private fun sendOtp(){
        viewModel.authResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    println("Sukses")
                }
                is Resource.Error -> {
                    println("Error")
                }
            }
        }
    }

    private fun observeVerifyOtp() {
        viewModel.verifyOtpResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> showNotif()
                is Resource.Success -> {
                    // Navigate to Home or another screen
                    appPreferences.setIsLoggedIn("true")
                    startActivity(Intent(this, CoreActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message ?: "OTP verification failed", Toast.LENGTH_SHORT).show()
                }

                else -> {Log.d(TAG, "observeVerifyOtp: else")}
            }
        }
    }

    private fun setupOtpInput() {
        val otpFields = listOf(binding.pinView1, binding.pinView2, binding.pinView3, binding.pinView4)

        otpFields.forEachIndexed { index, editText ->
            Log.d(TAG, "otpField")
            editText.isCursorVisible = false

            editText.inputType = InputType.TYPE_NULL
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.showSoftInputOnFocus = false

            editText.setOnFocusChangeListener { view, hasFocus ->
                view.animate().scaleX(if (hasFocus) 1.1f else 1f)
                    .scaleY(if (hasFocus) 1.1f else 1f)
                    .setDuration(150)
                    .start()
            }

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        if (index < otpFields.size - 1) {
                            otpFields[index + 1].requestFocus() // Pindah ke field berikutnya
                        }

                        if (isOtpComplete(otpFields)) {
                            val otp = getOtpValue(otpFields)
                            val req = OtpRequest(otp)
                            Log.d(TAG, "afterTextChanged: $otp")
                            viewModel.verifyOtp(req)
                            observeVerifyOtp()
                        }
                    }
                }


                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            editText.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && editText.text.isEmpty() && index > 0) {
                    otpFields[index - 0].requestFocus()
                    return@setOnKeyListener true
                }
                false
            }
        }
    }


    private fun updateOtpViews() {
        try {
            Log.d(TAG, "updateOtpViews called, otpDigits: $otpDigits")
            val currentOtp = otpDigits.joinToString("")

            binding.pinView1.setText(if (currentOtp.isNotEmpty()) currentOtp[0].toString() else "")
            binding.pinView2.setText(if (currentOtp.length > 1) currentOtp[1].toString() else "")
            binding.pinView3.setText(if (currentOtp.length > 2) currentOtp[2].toString() else "")
            binding.pinView4.setText(if (currentOtp.length > 3) currentOtp[3].toString() else "")
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateOtpViews", e)
        }
    }

    private fun isOtpComplete(otpFields: List<EditText>): Boolean {
        return otpFields.all { !it.text.isNullOrEmpty() }
    }

    private fun getOtpValue(otpFields: List<EditText>): String {
        return otpFields.joinToString("") { it.text.toString() }
    }

    private fun setupOtpTimer() {
        // Reset timer jika sudah berjalan
        otpTimer?.cancel()

        // Set button reset tidak aktif
        binding.resetButton.isEnabled = false

        // Mulai timer 60 detik (1 menit)
        otpTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.resetButton.text = "Resend Code (00:$secondsRemaining)"
            }

            override fun onFinish() {
                val buttonColorMain = ContextCompat.getColor(this@OtpActivity, R.color.main_color_500)
                binding.resetButton.backgroundTintList = ColorStateList.valueOf(buttonColorMain)
                binding.resetButton.isEnabled = true
                binding.resetButton.text = "Resend Code"
            }
        }.start()
    }

    private fun onNumberPressed(number: Char) {
        try {
            if (otpDigits.size < maxOtpLength) {
                otpDigits.add(number)
                updateOtpViews()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onNumberPressed", e)
        }
    }



    private fun onBackspacePressed() {
        if (otpDigits.isNotEmpty()) {
            otpDigits.removeAt(otpDigits.size - 1)
            updateOtpViews()
        }
    }

    private fun showLoadingWithLottie(){
        val dialogLoading = createLoadingDialog(this);
        dialogLoading.show()
    }

    private fun showNotif() {
        showCustomStatusDialog(
            title = "Berhasil",
            message = "Yeay! Akun kamu udah siap. Kita bakal langsung bawa kamu ke Home sebentar lagi~",
            useImageIcon = false,
            lottieRawResId = R.raw.person,
            isLoading = true
        ) {
            // Aksi saat tombol diklik
            Toast.makeText(this, "Login diklik!", Toast.LENGTH_SHORT).show()
        }
    }
}
