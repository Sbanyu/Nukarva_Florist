package com.example.nukarva_florist.ui.address

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nukarva_florist.data.model.Address
import com.example.nukarva_florist.data.req.AddressRequest
import com.example.nukarva_florist.databinding.ActivityAddAddressBinding
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class AddEditAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding
    private val viewModel: AddressViewModel by viewModels()
    private var isEditMode = false
    private var addressId: Int? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var addressPinPoint: String? = ""
    private var kotaPinPoint: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.btnFavorite.visibility = View.GONE

        // Cek apakah intent mengandung data address (edit mode)
        val address = intent.getParcelableExtra<Address>("address")

        if (address != null) {
            isEditMode = true
            addressId = address.id
            binding.toolbar.toolbarTitle.text = "Edit Alamat"
            binding.btnSaveAddress.text = "Update Alamat"
            populateForm(address)
        } else {
            binding.toolbar.toolbarTitle.text = "Tambah Alamat"
            binding.btnSaveAddress.text = "Simpan Alamat"
        }

        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.rlPinPoint.setOnClickListener{
            val intent = Intent(this, ChoosePinpointActivity::class.java)
            if (address?.latitude != null && address?.longitude != null &&
                address.latitude != 0.0 && address.longitude != 0.0) {

                intent.putExtra("latitude", address.latitude)
                intent.putExtra("longitude", address.longitude)

            } else {
                intent.putExtra("input_address", address?.fullAddress)
            }

            Log.d("address", address?.fullAddress.toString())
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_PINPOINT)
        }

        setupListeners()
        observeViewModel()
    }

    private fun populateForm(address: Address) {
        binding.etRecipientName.setText(address.recipientName)
        binding.etPhoneNumber.setText(address.phoneNumber)
        binding.etLabel.setText(address.label)
        binding.etCity.setText(address.city)
        binding.etKodePos.setText(address.postalCode)
        binding.etFullAddress.setText(address.fullAddress)
        binding.etNotes.setText(address.notes)
        if (address.addressLine.toString().isNotEmpty()){
            binding.tvLabelAddress.text = address.addressLine
        } else{
            binding.tvLabelAddress.text = "Add Pinpoint"
        }

        binding.cbMakeMain.isChecked = address.isMain
    }

    private fun setupListeners() {
        binding.btnSaveAddress.setOnClickListener {
            val address = collectAddressInput() ?: return@setOnClickListener

            if (isEditMode && addressId != null) {
                viewModel.updateAddress(addressId!!, address)
            } else {
                viewModel.addAddress(address)
            }
        }
    }

    private fun collectAddressInput(): AddressRequest? {
        val name = binding.etRecipientName.text.toString()
        val phone = binding.etPhoneNumber.text.toString()
        val label = binding.etLabel.text.toString()
        val city = binding.etCity.text.toString()
        val postalCode = binding.etKodePos.text.toString()
        val fullAddress = binding.etFullAddress.text.toString()
        val notes = binding.etNotes.text.toString()
        val isMain = binding.cbMakeMain.isChecked

        val allowedCities = listOf(
            "jakarta", "bogor", "depok", "tangerang", "bekasi"
        )

        if (name.isBlank() || phone.isBlank() || label.isBlank() || city.isBlank() || fullAddress.isBlank()) {
            Toast.makeText(this, "Mohon lengkapi alamat anda", Toast.LENGTH_SHORT).show()
            return null
        }

        if (allowedCities.none { city.trim().lowercase().contains(it) }) {
            Toast.makeText(this, "Maaf kami belum tersedia, kami akan hadir diarea kamu", Toast.LENGTH_SHORT).show()
            return null
        }

        return AddressRequest(
            label = label,
            addressLine = addressPinPoint.toString(),
            recipientName = name,
            phoneNumber = phone,
            city = city,
            fullAddress = fullAddress,
            notes = notes,
            pinpoint = kotaPinPoint.toString(),
            isMain = isMain,
            postalCode = postalCode,
            country = "Indonesia",
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0
        )
    }

    private fun observeViewModel() {
        viewModel.addAddressResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Toast.makeText(this, "Address added!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.updateAddressResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Toast.makeText(this, "Address updated!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                null -> TODO()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_PINPOINT && resultCode == RESULT_OK && data != null) {
            latitude = data.getDoubleExtra("latitude", 0.0)
            longitude = data.getDoubleExtra("longitude", 0.0)
            kotaPinPoint = data.getStringExtra("kota_kecamatan") ?: ""
            addressPinPoint = data.getStringExtra("address") ?: ""

            if (addressPinPoint != null) {
                binding.tvLabelAddress.text = addressPinPoint
            }else{
                binding.tvLabelAddress.text = "Add Pinpoint"
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CHOOSE_PINPOINT = 101
    }
}

