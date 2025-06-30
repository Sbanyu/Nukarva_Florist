package com.example.nukarva_florist.ui.address

import android.content.Intent
import com.example.nukarva_florist.viewmodel.AddressViewModel
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nukarva_florist.data.model.Address
import com.example.nukarva_florist.data.req.AddressRequest
import com.example.nukarva_florist.databinding.ActivityAddressBinding
import com.example.nukarva_florist.ui.home.OrderActivity
import com.example.nukarva_florist.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private val viewModel: AddressViewModel by viewModels()
    private lateinit var adapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.toolbarTitle.text = "Alamat"
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        observeData()

        binding.toolbar.btnAdd.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivity(intent)
        }

        viewModel.updateAddressResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Tampilkan loading
                }
                is Resource.Success -> {
                    val intent = Intent(this, OrderActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Gagal menyimpan alamat: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        binding.btnSaveAddress.setOnClickListener {
            val selectedAddress = adapter.getSelectedAddress()
            if (selectedAddress != null) {
                if (selectedAddress.isMain) {
                    // Tidak perlu update karena sudah main
                    val intent = Intent(this, OrderActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                    return@setOnClickListener
                }

                val addressRequest = AddressRequest(
                    label = selectedAddress.label,
                    addressLine = selectedAddress.addressLine,
                    city = selectedAddress.city,
                    postalCode = selectedAddress.postalCode,
                    country = selectedAddress.country,
                    recipientName = selectedAddress.recipientName,
                    phoneNumber = selectedAddress.phoneNumber,
                    fullAddress = selectedAddress.fullAddress,
                    notes = selectedAddress.notes ?: "",
                    pinpoint = selectedAddress.pinpoint ?: "",
                    isMain = true,
                    latitude = selectedAddress.latitude,
                    longitude = selectedAddress.longitude
                )

                viewModel.updateAddress(selectedAddress.id, addressRequest)
            } else {
                Toast.makeText(this, "Silakan pilih alamat terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchAddresses()
    }

    private fun observeData() {
        viewModel.addresses.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // show loading
                }
                is Resource.Success -> {
                    result.data?.let { addressList ->
                        adapter = AddressAdapter(this, addressList)
                        binding.rvShippingAddress.layoutManager = LinearLayoutManager(this)
                        adapter.updateAddresses(addressList)
                        binding.rvShippingAddress.adapter = adapter
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAddresses()
    }

}