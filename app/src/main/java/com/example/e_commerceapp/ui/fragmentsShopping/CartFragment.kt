package com.example.e_commerceapp.ui.fragmentsShopping

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerceapp.R
import com.example.e_commerceapp.adapters.recycler.CartRecyclerAdapter
import com.example.e_commerceapp.databinding.FragmentCartBinding
import com.example.e_commerceapp.firebase.FirebaseCommon
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.util.VerticalItemDecoration
import com.example.e_commerceapp.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class CartFragment : Fragment() {

    private val binding by lazy { FragmentCartBinding.inflate(layoutInflater) }
    private val cartAdapter by lazy { CartRecyclerAdapter() }
    private val viewModel: CartViewModel by activityViewModels()
    private var totalPrice = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()
        collectCartProducts()
        collectTotalPrice()
        navigateToCartDetails()
        onPlusMinusClick()
        deleteDialog()
        navigateToBilling()

    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun collectCartProducts() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressPlusMinus.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressPlusMinus.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            binding.rvCart.visibility = View.INVISIBLE
                            binding.linear.visibility = View.INVISIBLE
                            binding.btnCheckout.visibility = View.INVISIBLE
                            binding.tvEmptyCart.visibility = View.VISIBLE
                            binding.imgEmptyBoxTexture.visibility = View.VISIBLE
                            binding.imgEmptyBox.visibility = View.VISIBLE
                        } else {
                            binding.tvEmptyCart.visibility = View.INVISIBLE
                            binding.imgEmptyBoxTexture.visibility = View.INVISIBLE
                            binding.imgEmptyBox.visibility = View.INVISIBLE
                            cartAdapter.differ.submitList(it.data)
                        }
                    }

                    is Resource.Error -> {
                        binding.progressPlusMinus.visibility = View.INVISIBLE
                    }

                    else -> Unit
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun collectTotalPrice() {
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    binding.tvTotalprice.text = "$ $price"
                }
            }
        }
    }

    private fun navigateToCartDetails() {
        cartAdapter.onProductClick = {
            val bundle = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }
    }

    private fun onPlusMinusClick() {
        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }
        cartAdapter.onMinusesClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }
    }

    private fun deleteDialog() {
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }.setPositiveButton("Yes") { dialog, _ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
    }

    private fun navigateToBilling() {
        binding.btnCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
                totalPrice,
                cartAdapter.differ.currentList.toTypedArray(),
                true
            )
            findNavController().navigate(action)
        }
    }
}