package com.example.nukarva_florist.ui.bottomsheet

import Product
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.nukarva_florist.R
import com.example.nukarva_florist.databinding.BottomSheetAddToCartBinding
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.CartViewModel
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductOrderBottomSheet(
    private val product: Product
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddToCartBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()
    private var quantity = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddToCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close button
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val buttonColor = ContextCompat.getColor(requireContext(), R.color.main_color_500)
        binding.btnAddToCart.backgroundTintList = ColorStateList.valueOf(buttonColor)

        bindProgressButton(binding.btnAddToCart)

        // Show product data
        binding.tvProductName.text = product.name
        binding.tvAvailability.text = "Available: ${product.stockQuantity} pcs"
        binding.tvPrice.text = product.getFormattedPrice()

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.urbanist_medium)

        Glide.with(requireContext())
            .load(product.getFirstImageUrl())
            .placeholder(R.drawable.img_example_plants)
            .into(binding.ivImgProduct)

        // Populate chip group dynamically
        val types = product.productSize
        types.forEachIndexed { index, type ->
            val chip = Chip(requireContext()).apply {
                text = type
                isCheckable = true
                isCheckedIconVisible = false
                isClickable = true
                isFocusable = true

                setPadding(16, 8, 16, 8)

                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                chipStrokeWidth = 1f
                chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.disable)

                textSize = 14f
                typeface?.let { setTypeface(it) }
            }
            if (index == 0){
                chip.isChecked = true
                chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.main_color_500)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                chip.chipStrokeWidth = 1f
                chip.chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.disable)
            }
            binding.chipGroupType.addView(chip)
            chip.chipCornerRadius = 24f
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    chip.animate().setDuration(150)
                        .withStartAction {
                            chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.main_color_500)
                            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            chip.chipStrokeWidth = 1f
                            chip.chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.disable)
                        }.start()
                } else {
                    chip.animate().setDuration(150)
                        .withStartAction {
                            chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
                            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            chip.chipStrokeWidth = 1f
                            chip.chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.disable)
                        }.start()
                }
            }
        }


        // Quantity logic
        binding.tvQuantity.text = quantity.toString()

        binding.btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        binding.btnIncrease.setOnClickListener {
            if (quantity < product.stockQuantity) {
                quantity++
                binding.tvQuantity.text = quantity.toString()
            } else {
                Toast.makeText(requireContext(), "Max stock reached", Toast.LENGTH_SHORT).show()
            }
        }

        // Add to cart action
        binding.btnAddToCart.setOnClickListener {
            val selectedType = binding.chipGroupType.children
                .filterIsInstance<Chip>()
                .firstOrNull { it.isChecked }?.text ?: "Regular"

            // Call API via ViewModel
            cartViewModel.addToCart(product.productId, quantity,selectedType.toString())

            cartViewModel.cartResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.btnAddToCart.isEnabled = false
                        binding.btnAddToCart.showProgress {
                            buttonText = null
                            progressColorRes = R.color.white
                        }
                    }
                    is Resource.Success -> {
                        binding.btnAddToCart.isEnabled = true
                        binding.btnAddToCart.hideProgress("Add to Cart")
                        Toast.makeText(requireContext(), "Added to cart!", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    is Resource.Error -> {
                        binding.btnAddToCart.isEnabled = true
                        binding.btnAddToCart.hideProgress("Add to Cart")
                        Toast.makeText(requireContext(), "Failed: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
