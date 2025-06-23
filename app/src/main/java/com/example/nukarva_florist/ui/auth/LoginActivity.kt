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
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.example.nukarva_florist.databinding.ActivityLoginBinding
import com.example.nukarva_florist.utils.AppUtil
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.AuthViewModel
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appPreferences = AppPreferences(this)

        val rootView = findViewById<View>(R.id.root_layout)
        val backgroundColor = (rootView.background as? ColorDrawable)?.color ?: Color.WHITE
        AppUtil.setStatusBarBasedOnBackground(this, backgroundColor)

        binding.tilEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.font_grey)))
        initForm()
        bindProgressButton(binding.btnSignIn)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            val request = AuthRequest(
                email = email,
                password = password
            )
            viewModel.login(request)
        }


        viewModel.authResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.btnSignIn.isEnabled = false
                    binding.btnSignIn.showProgress {
                        buttonText = null
                        progressColorRes = R.color.white
                    }
                }
                is Resource.Success -> {
                    binding.btnSignIn.isEnabled = true
                    binding.btnSignIn.hideProgress("Sign In")
                    result.data?.data?.token?.let { token ->
                        // Save token to preferences
                        appPreferences.setEmail(binding.etEmail.text.toString())
                        appPreferences.setToken(token)
                        Log.d("TOKEN", token ?: "")
                        // Navigate to main activity
                        startActivity(Intent(this, OtpActivity::class.java))
                        finish()
                    }
                }
                is Resource.Error -> {
                    binding.btnSignIn.isEnabled = true
                    binding.btnSignIn.hideProgress("Sign In")
                    if (result.message?.contains("Email ini belum terdaftar", ignoreCase = true) == true) {
                        binding.tilEmail.requestFocus()
                        binding.tilEmail.boxStrokeWidth = 1
                        binding.tilEmail.boxStrokeColor = ContextCompat.getColor(this, R.color.danger_500)
                        binding.tilEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.danger_500)))
                        binding.tilEmail.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.danger_500)))

                        binding.errorEmail.text = "Email ini belum terdaftar"
                        binding.errorEmail.visibility = View.VISIBLE
                    } else if(result.message?.contains("Password yang Anda masukkan salah", ignoreCase = true) == true) {
                        binding.tilPassword.requestFocus()
                        binding.tilPassword.boxStrokeWidth = 1
                        binding.tilPassword.boxStrokeColor = ContextCompat.getColor(this, R.color.danger_500)
                        binding.tilEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.danger_500)))
                        binding.tilEmail.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.danger_500)))

                        binding.errorPassword.text = "Password yang Anda masukkan salah"
                        binding.errorPassword.visibility = View.VISIBLE
                    } else {
                        binding.errorEmail.visibility = View.INVISIBLE
                        binding.errorPassword.visibility = View.INVISIBLE
                        Toast.makeText(this, result.message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Error -> TODO()
                is Resource.Loading -> TODO()
                is Resource.Success -> TODO()
            }
        }

    }

    private fun initForm() {
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
                errorTextView?.text = "Format email tidak valid"
                errorTextView?.visibility = View.VISIBLE
                ContextCompat.getColor(et.context, R.color.danger_500)
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
        val email = binding.etEmail.text?.toString()?.trim()
        val password = binding.etPassword.text?.toString()?.trim()

        val isEmailValid = !email.isNullOrEmpty() && isValidEmail(email)
        val isPasswordValid = !password.isNullOrEmpty() && password.length >= 8

        val isFormValid = isEmailValid && isPasswordValid


        // Warna tombol sesuai validitas
        val buttonColor = if (isFormValid) {
            ContextCompat.getColor(this, R.color.main_color_500)
        } else {
            ContextCompat.getColor(this, R.color.disable)
        }

        binding.btnSignIn.backgroundTintList = ColorStateList.valueOf(buttonColor)
        binding.btnSignIn.setTextColor(getColor(R.color.white))
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
                credentialManager.createCredential(this@LoginActivity, request)
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