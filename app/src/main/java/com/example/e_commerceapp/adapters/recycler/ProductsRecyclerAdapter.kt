package com.example.e_commerceapp.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerceapp.databinding.ProductRvItemBinding
import com.example.e_commerceapp.model.Product

class ProductsRecyclerAdapter : RecyclerView.Adapter<ProductsRecyclerAdapter.BestProductsRecyclerAdapterViewHolder>() {

    var onItemClick: ((Product) -> Unit)? = null

    inner class BestProductsRecyclerAdapterViewHolder(val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestProductsRecyclerAdapterViewHolder {
        return BestProductsRecyclerAdapterViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onBindViewHolder(holder: BestProductsRecyclerAdapterViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(product.images[0]).into(imgProduct)
            product.offerPercentage?.let {
                val remainingPricePercentage = 1f - it
                val priceAfterOffer = remainingPricePercentage * product.price
                tvNewPrice.text = "$${String.format("%.2f", priceAfterOffer)}"
                tvPrice.paintFlags = tvPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            }

            if (product.offerPercentage == null) tvNewPrice.visibility = View.INVISIBLE
            tvPrice.text = "$${product.price}"
            tvName.text = product.name

            holder.itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[position])
            }
        }
    }
}