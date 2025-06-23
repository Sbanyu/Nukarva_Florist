package com.example.nukarva_florist.ui.bottomsheet

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.model.CartItemResponse
import com.example.nukarva_florist.databinding.BottomSheetDeleteToCartBinding
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.CartViewModel
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteCartBottomSheet(
    private val item: CartItemResponse,
    private val onItemDeleted: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDeleteToCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDeleteToCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonColor = ContextCompat.getColor(requireContext(), R.color.main_color_500)
        binding.btnDelToCart.backgroundTintList = ColorStateList.valueOf(buttonColor)

        binding.btnClose.setOnClickListener { dismiss() }

        binding.tvProductName.text = item.productName
        binding.tvAvailability.text = "Quan" + item.quantity.toString()
        binding.tvPrice.text = "Rp %,.0f".format(item.price * item.quantity)
        binding.btnDelToCart.text = "Remove from Cart"

        bindProgressButton(binding.btnDelToCart)


        binding.btnDelToCart.setOnClickListener {
            viewModel.deleteCartItem(item)

            viewModel.deleteStatus.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.btnDelToCart.showProgress {
                            buttonText = null
                            progressColorRes = R.color.white
                        }
                        binding.btnDelToCart.isEnabled = false
                    }
                    is Resource.Success -> {
                        binding.btnDelToCart.hideProgress("Remove from Cart")
                        Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show()
                        onItemDeleted()
                        dismiss()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), result.message ?: "Failed to remove item", Toast.LENGTH_SHORT).show()
                        binding.btnDelToCart.isEnabled = true
                        binding.btnDelToCart.hideProgress("Remove from Cart")
                    }
                    else -> {}
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
