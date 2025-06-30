package com.example.nukarva_florist.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.model.PaymentMethod
import com.example.nukarva_florist.databinding.ItemPaymentBinding
import com.example.nukarva_florist.utils.AppUtil.toRupiahWithoutDecimal

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.ViewHolder>(
) {

    private val items = mutableListOf<PaymentMethod>()
    private var selectedPosition = -1

    fun setItems(newItems: List<PaymentMethod>) {
        items.clear()
        items.addAll(newItems)
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun getSelected(): PaymentMethod? {
        return if (selectedPosition in items.indices) items[selectedPosition] else null
    }

    inner class ViewHolder(val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaymentMethod, isSelected: Boolean) {
            binding.tvNameProviderPayment.text = item.provider
            binding.tvBalance.text =
                if (item.balance != null) item.balance.toRupiahWithoutDecimal() else "-"

            val context = binding.imgPembayaran.context
            Glide.with(context)
                .load(item.image?.replace("localhost", "10.0.2.2"))
                .placeholder(R.drawable.ic_wallet)
                .into(binding.imgPembayaran)

            binding.root.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()
            }

            binding.rbOptionsMethod.isChecked = isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPaymentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = items.size
}
