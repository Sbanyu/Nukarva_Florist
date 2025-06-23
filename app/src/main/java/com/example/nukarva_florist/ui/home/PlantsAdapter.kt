package com.example.nukarva_florist.ui.home

import Product
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.nukarva_florist.R
import com.example.nukarva_florist.databinding.ItemPlantBinding
import com.example.nukarva_florist.utils.AppUtil
import org.json.JSONArray

class PlantsAdapter(
    private val products: MutableList<Product>
) : RecyclerView.Adapter<PlantsAdapter.PlantViewHolder>() {

    private var appUtil: AppUtil? = null
    private var onItemClickListener: ((Product) -> Unit)? = null
    private var onFavoriteClickListener: ((Product, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnFavoriteClickListener(listener: (Product, Int) -> Unit) {
        onFavoriteClickListener = listener
    }

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun updateFavoriteState(isFavoriteProvider: (Int) -> Boolean) {
        products.forEach { it.isFavorite = isFavoriteProvider(it.productId) }
        notifyDataSetChanged()
    }

    fun toggleFavorite(position: Int) {
        if (position in products.indices) {
            products[position].isFavorite = !products[position].isFavorite
            notifyItemChanged(position)
        }
    }

    inner class PlantViewHolder(
        private val binding: ItemPlantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            with(binding) {
                // Load image with Glide
                val urls = JSONArray(product.imageUrls)
                val imageUrl = urls.getString(0)
                val fixedUrl = imageUrl.replace("localhost", "10.0.2.2")
                Glide.with(imgPlant.context)
                    .load(fixedUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.img_example_plants)
                            .transform(RoundedCorners(24))
                    )
                    .into(imgPlant)


                // Set product data
                tvPlantName.text = product.name
                tvPlantCategory.text = product.getCategoryName()

                // Show price with discount if available
                if (product.hasDiscount()) {
                    tvPlantPrice.text = product.getFormattedFinalPrice()
                    // You can add original price with strikethrough if needed
                } else {
                    tvPlantPrice.text = product.getFormattedPrice()
                }

                // Update favorite icon with animation
                updateFavoriteIcon(product.isFavorite, false)

                // Item click listener
                cvItemPlant.setOnClickListener {
                    onItemClickListener?.invoke(product)

                }

                tvPlantName.setOnClickListener {
                    onItemClickListener?.invoke(product)
                }

                tvPlantCategory.setOnClickListener {
                    onItemClickListener?.invoke(product)
                }

                tvPlantPrice.setOnClickListener {
                    onItemClickListener?.invoke(product)
                }

                // Favorite button click listener with animation
                btnFavorite.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onFavoriteClickListener?.invoke(product, position)
                    }
                }

            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean, animate: Boolean) {
            val context = binding.btnFavorite

            if (isFavorite) {
                binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
                if (animate) {
                    // Heart fill animation
                    appUtil?.animateHeartFill(context)
                }
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_heart_outline)
                if (animate) {
                    // Heart unfill animation
                    appUtil?.animateHeartUnfill(context)
                }
            }
        }
    }
}