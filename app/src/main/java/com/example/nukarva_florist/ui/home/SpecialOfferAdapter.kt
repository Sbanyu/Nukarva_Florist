package com.example.nukarva_florist.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nukarva_florist.data.model.Offer
import com.example.nukarva_florist.databinding.ItemOfferBinding

class SpecialOfferAdapter(private val images: List<Offer>) :
    RecyclerView.Adapter<SpecialOfferAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemOfferBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(offer: Offer) {
            binding.imgOffer.setImageResource(offer.imageResId)
            binding.tvDiscountTitle.text = offer.discountTitle
            binding.tvOfferDescription.text = offer.description        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOfferBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}
