package com.example.nukarva_florist.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.model.CartItemResponse
import com.example.nukarva_florist.databinding.ItemCartBinding
import org.json.JSONArray

class CartAdapter(
    private val items: MutableList<CartItemResponse>,
    private val onDelete: (CartItemResponse) -> Unit,
    private val onTotalChanged: (Double) -> Unit,
    private val onQuantityChange: (cartId: Int, newQuantity: Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvProductName.text = item.productName
            tvType.text = item.type ?: "-"
            tvQuantity.text = "${item.quantity}"
            tvPrice.text = "Rp %,.0f".format(item.quantity * item.price)

            // Checkbox state
            checkboxSelect.isChecked = item.isSelected
            checkboxSelect.setOnCheckedChangeListener { _, isChecked ->
                item.isSelected = isChecked
                onTotalChanged(calculateSelectedTotal())
            }

            // Load image
            val urls = JSONArray(item.imageUrls)
            val imageUrl = urls.optString(0).replace("localhost", "10.0.2.2")

            Glide.with(imgProduct.context)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.img_example_plants)
                        .transform(RoundedCorners(24))
                )
                .into(imgProduct)

            btnDelete.setOnClickListener {
                onDelete(item)
            }

            btnIncrease.setOnClickListener {
                item.quantity += 1
                tvQuantity.text = item.quantity.toString()
                onQuantityChange(item.id, item.quantity)
            }

            if (item.quantity == 1) {
                btnDecrease.isEnabled = false
                btnDecrease.setColorFilter(
                    ContextCompat.getColor(root.context, R.color.disable),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                btnDecrease.isEnabled = true
                btnDecrease.setColorFilter(
                    ContextCompat.getColor(root.context, R.color.main_color_500),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }


            btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity -= 1
                    tvQuantity.text = item.quantity.toString()
                    onQuantityChange(item.id, item.quantity)
                }
            }

        }
    }


    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItemResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun calculateSelectedTotal(): Double {
        return items.filter { it.isSelected }
            .sumOf { it.quantity * it.price }
    }
}
