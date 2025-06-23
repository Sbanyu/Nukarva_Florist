package com.example.nukarva_florist.ui.auth

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.AppPreferences
import com.example.nukarva_florist.data.req.AuthRequest
import com.example.nukarva_florist.databinding.ActivityRegisterBinding
import com.example.nukarva_florist.utils.AppUtil
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.AuthViewModel
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    lateinit var appPreferences: AppPreferences
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val rootView = findViewById<View>(R.id.root_layout)
        val backgroundColor = (rootView.background as? ColorDrawable)?.color ?: Color.WHITE
        AppUtil.setStatusBarBasedOnBackground(this, backgroundColor)
        appPreferences = AppPreferences(this)

        // Full Name
        binding.tilFullName.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.font_grey)))
        // Email
        binding.tilEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.font_grey)))
        // Password
        binding.tilPassword.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.font_grey)))
        initForm()
        bindProgressButton(binding.btnSignUp)
        binding.tvSignIn.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val fullname = binding.etFullName.text.toString()
            val password = binding.etPassword.text.toString().trim()

            val request = AuthRequest(
                email = email,
                fullname = fullname,
                password = password
            )
            viewModel.register(request)
        }

        viewModel.authResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.btnSignUp.isEnabled = false
                    binding.btnSignUp.showProgress {
                        buttonText = null
                        progressColorRes = R.color.white
                    }
                }
                is Resource.Success -> {
                    binding.btnSignUp.isEnabled = true
                    binding.btnSignUp.hideProgress("Sign Up")
                    result.data?.data?.token?.let { token ->
                        // Save token to preferences
                        appPreferences.setToken(token)
                        appPreferences.setEmail(binding.etEmail.text.toString().trim())
                        Log.d("TOKEN", token ?: "")
                        // Navigate to main activity
                        startActivity(Intent(this, OtpActivity::class.java))
                        finish()
                    }
                }
                is Resource.Error -> {
                    binding.btnSignUp.isEnabled = true
                    binding.btnSignUp.hideProgress("Sign Up")
                    if (result.message?.contains("Email sudah digunakan", ignoreCase = true) == true) {
                        binding.tilEmail.requestFocus()
                        binding.tilEmail.boxStrokeWidth = 1
                        binding.tilEmail.boxStrokeColor = ContextCompat.getColor(this, R.color.danger_500)
                        binding.tilEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.danger_500)))
                        binding.tilEmail.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.danger_500)))

                        binding.errorEmail.text = "Email sudah digunakan"
                        binding.errorEmail.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(this, result.message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun initForm() {
        setFieldFocusListener(binding.tilFullName, binding.etFullName, ContextCompat.getColor(this, R.color.font_grey), ContextCompat.getColor(this, R.color.main_color_500))
        setFieldFocusListener(binding.tilEmail, binding.etEmail, ContextCompat.getColor(this, R.color.font_grey), ContextCompat.getColor(this, R.color.main_color_500)
            ,isEmailField = true
            ,errorTextView = binding.errorEmail
        )
        setFieldFocusListener(binding.tilPassword, binding.etPassword, ContextCompat.getColor(this, R.color.font_grey), ContextCompat.getColor(this, R.color.main_color_500))
        AppUtil.setupPasswordVisibilityToggle(this, binding.tilPassword, binding.etPassword)

        binding.cbRemember.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                savePassword(email, password)
            }
        }
    }

    // Function to handle focus and text changes for multiple fields
    private fun setFieldFocusListener(
        til: TextInputLayout, et: EditText,
        defaultColor: Int, activeColor: Int,
        isEmailField: Boolean = false,
        errorTextView: TextView? = null,
    ) {
        til.boxStrokeWidth = 0

        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateBorderState(til, et, defaultColor, activeColor, isEmailField, errorTextView = binding.errorEmail)
                validateForm()
            }

            override fun afterTextChanged(s: Editable?) {
                updateBorderState(til, et, defaultColor, activeColor, isEmailField, errorTextView = binding.errorEmail)
            }
        })

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            updateBorderState(til, et, defaultColor, activeColor, isEmailField, hasFocus, errorTextView = binding.errorEmail)
        }
    }


    // Function to update border state & icon tint
    private fun updateBorderState(
        til: TextInputLayout, et: EditText,
        defaultColor: Int, activeColor: Int,
        isEmailField: Boolean = false,
        forceFocus: Boolean? = null,
        errorTextView: TextView? = null,
    ) {
        val hasFocus = forceFocus ?: et.hasFocus()
        val text = et.text?.toString() ?: ""

        val isValid = if (isEmailField) isValidEmail(text) else true
        val hasText = text.isNotEmpty()

        // Warna border berdasarkan kondisi
        val borderColor = when {
            isEmailField && hasText && !isValid -> {
                errorTextView?.text = "Format email tidak valid" // Tampilkan error
                errorTextView?.visibility = View.VISIBLE
                ContextCompat.getColor(et.context, R.color.danger_500) // Warna merah jika email tidak valid
            }
            hasFocus || hasText -> {
                errorTextView?.visibility = View.INVISIBLE // Sembunyikan error jika valid
                activeColor
            }
            else -> {
                errorTextView?.visibility = View.INVISIBLE // Sembunyikan error jika kosong
                defaultColor
            }
        }

        // Update UI
        til.boxStrokeWidth = if (hasFocus) 1 else 0
        til.boxStrokeColor = borderColor
        til.setStartIconTintList(ColorStateList.valueOf(borderColor))
        til.setEndIconTintList(ColorStateList.valueOf(borderColor))
    }




    private fun validateForm() {
        val fullName = binding.etFullName.text?.toString()?.trim()
        val email = binding.etEmail.text?.toString()?.trim()
        val password = binding.etPassword.text?.toString()?.trim()

        val isFullNameValid = !fullName.isNullOrEmpty()
        val isEmailValid = !email.isNullOrEmpty() && isValidEmail(email)
        val isPasswordValid = !password.isNullOrEmpty() && password.length >= 8

        val isFormValid = isFullNameValid && isEmailValid && isPasswordValid

        binding.btnSignUp.isEnabled = isFormValid

        // Warna tombol sesuai validitas
        val buttonColor = if (isFormValid) {
            ContextCompat.getColor(this, R.color.main_color_500)
        } else {
            ContextCompat.getColor(this, R.color.disable)
        }

        binding.btnSignUp.backgroundTintList = ColorStateList.valueOf(buttonColor)
        binding.btnSignUp.setTextColor(getColor(R.color.white))
    }


    private fun navigateToOtpSignUp() {
        binding.btnSignUp.setOnClickListener {
            if (binding.btnSignUp.isEnabled) {
                Toast.makeText(this, "Masuk Nih Pak Haji", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk validasi email dengan regex
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun savePassword(email: String, password: String) {
        if (!isCredentialManagerAvailable()) {
            Log.e("CredentialManager", "Credential Manager is not available on this device.")
            return
        }

        val credentialManager = CredentialManager.create(this)
        val request = CreatePasswordRequest(email, password)

        lifecycleScope.launch {
            try {
                credentialManager.createCredential(this@RegisterActivity, request)
                Toast.makeText(applicationContext, "Password Saved!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("CredentialManager", "Error saving credentials", e)
            }
        }
    }

    private fun isCredentialManagerAvailable(): Boolean {
        val packageManager = packageManager
        val intent = Intent("android.credentials.action.CREATE_CREDENTIAL")
        return packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null
    }

}
