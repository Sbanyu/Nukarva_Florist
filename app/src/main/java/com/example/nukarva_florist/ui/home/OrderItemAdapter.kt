package com.example.nukarva_florist.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.resp.CartItemResponse
import com.example.nukarva_florist.databinding.ItemOrderBinding
import com.example.nukarva_florist.di.AppModule
import com.example.nukarva_florist.utils.AppUtil.toRupiahWithoutDecimal
import org.json.JSONArray

class OrderItemAdapter(
    private val items: List<CartItemResponse>
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    inner class OrderItemViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvProductName.text = item.productName
            tvQuantity.text = "Qty: ${item.quantity}"
            tvPrice.text = (item.quantity * item.price).toRupiahWithoutDecimal()

            val imageUrl = JSONArray(item.imageUrls).optString(0).replace("localhost", "10.0.2.2")
            Glide.with(imgProduct.context)
                .load(imageUrl)
                .apply(RequestOptions().placeholder(R.drawable.img_example_plants).transform(
                    RoundedCorners(16)
                ))
                .into(imgProduct)
        }
    }

    override fun getItemCount(): Int = items.size
}
