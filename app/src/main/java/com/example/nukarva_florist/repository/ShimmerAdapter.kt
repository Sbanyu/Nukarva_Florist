package com.example.nukarva_florist.repository

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nukarva_florist.databinding.ItemProductShimmerBinding

class ShimmerAdapter : RecyclerView.Adapter<ShimmerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductShimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = 6

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

    inner class ViewHolder(binding: ItemProductShimmerBinding) : RecyclerView.ViewHolder(binding.root)
}
