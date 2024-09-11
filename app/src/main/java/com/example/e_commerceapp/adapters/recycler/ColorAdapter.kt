package com.example.e_commerceapp.adapters.recycler

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerceapp.databinding.ColorItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsAndSizesAdapterViewHolder>() {

    private var selectedPosition = -1
    var onItemClick: ((Int) -> Unit)? = null

    inner class ColorsAndSizesAdapterViewHolder(val binding: ColorItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(color: Int, position: Int) {
            val imageDrawable = ColorDrawable(color)
            binding.imgContent.setImageDrawable(imageDrawable)
            if (position == selectedPosition) { // Color selected
                binding.apply {
                    imgShadow.visibility = View.VISIBLE
                    imgDone.visibility = View.VISIBLE
                }
            } else { // Color not selected
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE
                    imgDone.visibility = View.INVISIBLE
                }
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(position)
                onItemClick?.invoke(color)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsAndSizesAdapterViewHolder {
        return ColorsAndSizesAdapterViewHolder(ColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ColorsAndSizesAdapterViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)
    }
}