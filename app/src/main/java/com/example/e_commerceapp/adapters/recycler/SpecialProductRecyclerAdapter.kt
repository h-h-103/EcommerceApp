package com.example.e_commerceapp.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerceapp.databinding.SpecialRvItemBinding
import com.example.e_commerceapp.model.Product

class SpecialProductRecyclerAdapter : RecyclerView.Adapter<SpecialProductRecyclerAdapter.SpecialProductViewHolder>() {

    var onItemClick: ((Product) -> Unit)? = null
    var onAddToCartClick: ((Product) -> Unit)? = null

    inner class SpecialProductViewHolder(val binding: SpecialRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallBack = object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductViewHolder {
        return SpecialProductViewHolder(
            SpecialRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SpecialProductViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {
            Glide.with(holder.itemView).load(product.images[0]).into(imgAd)
            tvAdPrice.text = "$${product.price}"
            tvAdName.text = product.name
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }

        holder.binding.btnAddToCart.setOnClickListener {
            onAddToCartClick?.invoke(product)
        }
    }
}