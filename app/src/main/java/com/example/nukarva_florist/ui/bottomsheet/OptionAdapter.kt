package com.example.nukarva_florist.ui.bottomsheet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nukarva_florist.databinding.ItemOptionBinding

class OptionAdapter(
    private val options: List<String>,
    private var selected: String?,
    private val onSelected: (String) -> Unit
) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    inner class OptionViewHolder(val binding: ItemOptionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        with(holder.binding) {
            tvOptions.text = option
            rbOptions.isChecked = option == selected

            rbOptions.setOnClickListener {
                selected = option
                notifyDataSetChanged()
                onSelected(option)
            }

            clRoot.setOnClickListener {
                selected = option
                notifyDataSetChanged()
                onSelected(option)
            }
        }
    }

    override fun getItemCount() = options.size
}
