package com.example.nukarva_florist.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.resp.CartItemResponse
import com.example.nukarva_florist.databinding.ActivityCartLayoutBinding
import com.example.nukarva_florist.ui.bottomsheet.DeleteCartBottomSheet
import com.example.nukarva_florist.utils.AppUtil.toRupiahWithoutDecimal
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: ActivityCartLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItemResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCartLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
            .isAppearanceLightStatusBars = true

        setupToolbar()
        setupRecyclerView()
        setupBottomSummary()
        observeCartData()

        viewModel.fetchCart()
    }

    private fun setupToolbar() {
        binding.toolbar.btnBack.visibility = View.GONE
        binding.toolbar.toolbarTitle.text = "Cart"
        binding.toolbar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onDelete = { selectedItem ->
                val bottomSheet = DeleteCartBottomSheet(
                    item = selectedItem,
                    onItemDeleted = {
                        viewModel.fetchCart()
                    }
                )
                parentFragmentManager.let {
                    bottomSheet.show(it, "DeleteCartSheet")
                }
            },
            onTotalChanged = { total -> updateTotalPrice(total) },
            onQuantityChange = { cartId, newQty ->
                viewModel.updateCartItem(cartId, newQty)
            }
        )

        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun observeCartData() {
        viewModel.cartItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }
                is Resource.Success -> {
                    result.data?.let { items ->
                        Log.d("CartFragment", "Items received: ${items.size}")
                        cartAdapter.updateItems(items)
                        updateTotalPrice(cartAdapter.calculateSelectedTotal())
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), result.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.deleteStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    viewModel.fetchCart() // refresh cart
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), result.message ?: "Delete error", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun updateTotalPrice(total: Double) {

        binding.tvTotalPrice.text = total.toRupiahWithoutDecimal()
        binding.btnOrder.isEnabled = total > 0
        binding.btnOrder.backgroundTintList =
            if (total > 0) requireContext().getColorStateList(R.color.main_color_500)
            else requireContext().getColorStateList(R.color.disable)
    }

    private fun setupBottomSummary() {
        binding.btnOrder.setOnClickListener {
            val selectedItems = cartItems.filter { it.isSelected }
            val intent = Intent(requireContext(), OrderActivity::class.java)
            intent.putParcelableArrayListExtra("selected_items", ArrayList(selectedItems))
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
