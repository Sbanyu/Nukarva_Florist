package com.example.nukarva_florist.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.model.Shipment
import com.example.nukarva_florist.data.req.AddressRequest
import com.example.nukarva_florist.data.req.OrderItemRequest
import com.example.nukarva_florist.data.req.OrderRequest
import com.example.nukarva_florist.data.resp.CartItemResponse
import com.example.nukarva_florist.databinding.ActivityOrderBinding
import com.example.nukarva_florist.ui.address.AddressActivity
import com.example.nukarva_florist.utils.AppUtil.toRupiahWithoutDecimal
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.AddressViewModel
import com.example.nukarva_florist.viewmodel.OrderViewModel
import com.example.nukarva_florist.viewmodel.PaymentViewModel
import com.example.nukarva_florist.viewmodel.ShipmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var cartItems: List<CartItemResponse>
    private lateinit var orderAdapter: OrderItemAdapter
    private lateinit var paymentAdapter: PaymentAdapter
    private val viewModel: AddressViewModel by viewModels()
    private val shipmentViewModel: ShipmentViewModel by viewModels()
    private val paymentViewModel: PaymentViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Terima data dari intent
        cartItems = intent.getParcelableArrayListExtra<CartItemResponse>("selected_items") ?: emptyList()

        setupRecyclerView()
        setupListeners()
        setUpToolbar()
        observeData()
        viewModel.fetchAddresses()
        shipmentViewModel.fetchShipments()
        paymentViewModel.fetchPaymentMethods()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setUpToolbar(){
        binding.toolbar.toolbarTitle.setText("Order")
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.btnFavorite.visibility = View.GONE
    }
    private fun setupRecyclerView() {
        orderAdapter = OrderItemAdapter(cartItems)
        binding.tvOrderItems.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = orderAdapter
        }

        binding.tvCountProduct.text = "${cartItems.size} Items"

        paymentAdapter = PaymentAdapter()
        binding.rvMethodPembayaran.layoutManager = LinearLayoutManager(this)
        binding.rvMethodPembayaran.adapter = paymentAdapter

    }

    @SuppressLint("SetTextI18n")
    private fun calculateOrderSummary(shipment: Shipment) {
        val subtotal = cartItems.sumOf { it.quantity * it.price }
        val deliveryCost = shipment.cost
        val appFees = 2000.0
        val discount = 0.0
        val total = subtotal + deliveryCost + appFees - discount

        binding.tvSubTotal.text = (subtotal).toRupiahWithoutDecimal()
        binding.tvDeliveryCost.text = (deliveryCost).toRupiahWithoutDecimal()
        binding.tvAppFee.text = (appFees).toRupiahWithoutDecimal()
        binding.tvDiscount.text = "-" + (discount).toRupiahWithoutDecimal()
        binding.tvTotalPayment.text = (total).toRupiahWithoutDecimal()
        binding.tvTotalPrice.text = (total).toRupiahWithoutDecimal()

        binding.btnOrder.isEnabled = total > 0
        binding.btnOrder.backgroundTintList =
            if (total > 0) getColorStateList(R.color.main_color_500)
            else getColorStateList(R.color.disable)
    }

    private fun setupListeners() {
        binding.btnOrder.setOnClickListener {
            val orderRequest = collectOrderRequest()
            if (orderRequest != null) {
                orderViewModel.createOrder(orderRequest)
            }
        }

        binding.tvChangeAddress.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }


        binding.tvChangeShipment.setOnClickListener {
            Toast.makeText(this, "Ganti metode pengiriman", Toast.LENGTH_SHORT).show()
        }

        binding.tvChangePaymentMethod.setOnClickListener {
            Toast.makeText(this, "Ganti metode pembayaran", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        viewModel.addresses.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // show loading
                }
                is Resource.Success -> {
                    val mainAddress = result.data?.firstOrNull { it.isMain }
                    Log.d("cekData",mainAddress.toString())
                    if (mainAddress != null) {
                        binding.tvLabelAddress.text = mainAddress.label
                        binding.tvSubLabelAddress.text = mainAddress.fullAddress
                    } else {
                        binding.tvLabelAddress.text = "No main address"
                        binding.tvSubLabelAddress.text = ""
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        shipmentViewModel.shipments.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // show loading
                }
                is Resource.Success -> {
                    val shipmentList = result.data ?: emptyList()
                    if (shipmentList != null) {
                        binding.tvNamaProvider.text = shipmentList[0].providerName
                        binding.tvSubProvider.text = shipmentList[0].deliveryTime
                        binding.tvPriceProvider.text = shipmentList[0].cost.toRupiahWithoutDecimal()

                        calculateOrderSummary(shipmentList[0])
                        val imageUrl = shipmentList[0].image?.replace("localhost", "10.0.2.2")

                        if (imageUrl != null) {
                            Glide.with(binding.imgShipment.context)
                                .load(imageUrl)
                                .apply(
                                    RequestOptions()
                                        .placeholder(R.drawable.img_example_plants)
                                        .transform(RoundedCorners(24))
                                )
                                .into(binding.imgShipment)
                        } else {
                            // Fallback jika image null
                            binding.imgShipment.setImageResource(R.drawable.ic_logo_pxp)
                        }
                    } else {
                        binding.tvLabelAddress.text = "-"
                        binding.tvSubLabelAddress.text = "-"
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        paymentViewModel.paymentMethods.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show shimmer
                }
                is Resource.Success -> {
                    result.data?.let {
                        val onlyOne = it.take(1)
                        paymentAdapter.setItems(onlyOne)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        orderViewModel.createOrderResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // tampilkan loading
                }
                is Resource.Success -> {
                    val intent = Intent(this, PaymentSuccessActivity::class.java)
                    startActivity(intent)
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun collectOrderRequest(): OrderRequest? {
        val mainAddress = viewModel.addresses.value?.data?.firstOrNull { it.isMain }
        val selectedShipment = shipmentViewModel.shipments.value?.data?.firstOrNull()
        val selectedPayment = paymentViewModel.paymentMethods.value?.data?.firstOrNull()

        if (mainAddress == null || selectedShipment == null || selectedPayment == null) {
            Toast.makeText(this, "Data alamat, pengiriman, atau pembayaran belum lengkap", Toast.LENGTH_SHORT).show()
            return null
        }

        val subtotal = cartItems.sumOf { it.quantity * it.price }
        val shippingCost = selectedShipment.cost
        val appFees = 2000.0
        val discount = 0.0
        val total = subtotal + shippingCost + appFees - discount

        val itemRequests = cartItems.map {
            OrderItemRequest(
                productId = it.plantId.toLong(),
                quantity = it.quantity,
                unitPrice = it.price,
                discountPrice = 0.0
            )
        }

        return OrderRequest(
            addressId = mainAddress.id.toLong(),
            shipmentId = selectedShipment.id.toLong(),
            paymentMethodId = selectedPayment.id,
            subtotal = subtotal,
            shippingCost = shippingCost,
            applicationFees = appFees,
            discount = discount,
            totalPrice = total,
            items = itemRequests
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAddresses()
    }

}

