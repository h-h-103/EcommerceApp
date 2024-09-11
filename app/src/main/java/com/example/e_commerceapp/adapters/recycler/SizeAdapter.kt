package com.example.e_commerceapp.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerceapp.databinding.SizeItemBinding

class SizeAdapter : RecyclerView.Adapter<SizeAdapter.SizeAdapterViewHolder>() {

    private var selectedPosition = -1
    var onItemClick: ((String) -> Unit)? = null

    inner class SizeAdapterViewHolder(val binding: SizeItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(size: String, position: Int) {
            binding.tvSize.text = size
            if (position == selectedPosition) { // Size selected
                binding.apply {
                    imgShadow.visibility = View.VISIBLE
                }
            } else { // Size not selected
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE
                }
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition) // Notify the previously selected item to update its UI
                notifyItemChanged(position) // Notify the newly selected item to update its UI
                onItemClick?.invoke(size) // Trigger the click callback
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeAdapterViewHolder {
        return SizeAdapterViewHolder(SizeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SizeAdapterViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)
    }
}