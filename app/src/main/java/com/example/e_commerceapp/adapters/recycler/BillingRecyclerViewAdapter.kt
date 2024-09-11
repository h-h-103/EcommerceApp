package com.example.e_commerceapp.adapters.recycler

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerceapp.databinding.BillingProductsRvItemBinding
import com.example.e_commerceapp.helper.getProductPrice
import com.example.e_commerceapp.model.CartProduct

class BillingRecyclerViewAdapter: RecyclerView.Adapter<BillingRecyclerViewAdapter.BillingViewHolder>() {

    var onItemClick: ((CartProduct) -> Unit)? = null

    inner class BillingViewHolder(private val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("DefaultLocale", "SetTextI18n")
            fun bind(billingProduct: CartProduct) {
                binding.apply {
                    Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                    tvProductCartName.text = billingProduct.product.name
                    tvBillingProductQuantity.text = billingProduct.quantity.toString()

                    val priceAfterPercentage = billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                    tvProductCartPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"

                    imageCartProductColor.setImageDrawable(ColorDrawable(billingProduct.selectedColor?: Color.TRANSPARENT))
                    tvCartProductSize.text = billingProduct.selectedSize?:"".also {
                        imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                    }
                }
            }
        }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingViewHolder {
        return BillingViewHolder(BillingProductsRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: BillingViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }
}