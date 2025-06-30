package com.example.nukarva_florist.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nukarva_florist.databinding.ActivityPaymentSuccessBinding

class PaymentSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Tombol kembali
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Tombol "Beli Lagi" -> kembali ke halaman utama
        binding.btnBuyAgain.setOnClickListener {
            val intent = Intent(this, CoreActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // Tombol "Lihat Transaksi"
        binding.btnSeeTransaction.setOnClickListener {
            val intent = Intent(this, CoreActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CoreActivity::class.java) // Ganti dengan halaman tujuan
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish() // Tutup activity ini
    }
}
