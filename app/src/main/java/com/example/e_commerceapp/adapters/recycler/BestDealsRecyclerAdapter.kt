package com.example.e_commerceapp.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerceapp.databinding.BestDealsRvItemBinding
import com.example.e_commerceapp.model.Product

class BestDealsRecyclerAdapter : RecyclerView.Adapter<BestDealsRecyclerAdapter.BestDealsRecyclerAdapterViewHolder>() {

    var onItemClick: ((Product) -> Unit)? = null

    inner class BestDealsRecyclerAdapterViewHolder(val binding: BestDealsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallBack = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsRecyclerAdapterViewHolder {
        return BestDealsRecyclerAdapterViewHolder(BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int = differ.currentList.size

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onBindViewHolder(holder: BestDealsRecyclerAdapterViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(product.images[0]).into(imgBestDeal)
            product.offerPercentage?.let {
                val remainingPricePercentage = 1f - it
                val priceAfterOffer = remainingPricePercentage * product.price
                tvNewPrice.text = "$${String.format("%.2f", priceAfterOffer)}"
            }
            tvOldPrice.text = "$${product.price}"
            tvDealProductName.text = product.name

        }

        holder.binding.btnSeeProduct.setOnClickListener {
            onItemClick?.invoke(product)
        }
    }
}