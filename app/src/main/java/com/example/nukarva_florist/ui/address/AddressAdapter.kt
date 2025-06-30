package com.example.nukarva_florist.ui.address

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nukarva_florist.data.model.Address
import com.example.nukarva_florist.databinding.ItemAddressBinding

class AddressAdapter(
    private val context: Context,
    private var addresses: List<Address>
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root)
    var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemAddressBinding.inflate(inflater, parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = addresses[position]
        holder.binding.tvLabel.text = item.label
        holder.binding.tvPhone.text = if (item.phoneNumber.isNotEmpty()) item.phoneNumber else "-"
        holder.binding.tvAddress.text = item.fullAddress

        if (item.isMain && selectedPosition == -1) {
            selectedPosition = position
        }

        holder.binding.radioSelect.isChecked = position == selectedPosition

        // Pilih saat item di-tap
        holder.binding.root.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }

        // Edit button
        holder.binding.btnChangeAddress.setOnClickListener {
            val selectedAddress = addresses[holder.adapterPosition]
            val intent = Intent(context, AddEditAddressActivity::class.java)
            intent.putExtra("address", selectedAddress)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = addresses.size


    fun getSelectedAddress(): Address? {
        return if (selectedPosition != -1) addresses[selectedPosition] else null
    }

    fun updateAddresses(newList: List<Address>) {
        // Sort: yang isMain true ada di paling atas
        addresses = newList.sortedByDescending { it.isMain }

        // Set selectedPosition ke index pertama dari isMain
        selectedPosition = addresses.indexOfFirst { it.isMain }

        notifyDataSetChanged()
    }

}