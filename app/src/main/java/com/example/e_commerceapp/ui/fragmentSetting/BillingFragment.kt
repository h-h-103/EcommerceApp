package com.example.e_commerceapp.ui.fragmentSetting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerceapp.R
import com.example.e_commerceapp.adapters.recycler.AddressAdapter
import com.example.e_commerceapp.adapters.recycler.BillingRecyclerViewAdapter
import com.example.e_commerceapp.databinding.FragmentBillingBinding
import com.example.e_commerceapp.model.Address
import com.example.e_commerceapp.model.CartProduct
import com.example.e_commerceapp.model.order.Order
import com.example.e_commerceapp.model.order.OrderStatus
import com.example.e_commerceapp.util.HorizontallItemDecoration
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.viewmodel.BillingViewModel
import com.example.e_commerceapp.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class BillingFragment : Fragment() {

    private val binding by lazy { FragmentBillingBinding.inflate(layoutInflater) }
    private val viewModel: BillingViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private val navArgs by navArgs<BillingFragmentArgs>()
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var billingAdapter: BillingRecyclerViewAdapter
    private var selectedAddress: Address? = null
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        products = navArgs.product.toList()
        totalPrice = navArgs.totalPrice

        if (!navArgs.payment) {
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
        }

        setupAddressRecyclerView()
        setupBillingRecyclerView()
        collectAddress()
        navigateToAddress()
        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAddressRecyclerView() {
        addressAdapter = AddressAdapter()
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontallItemDecoration())
        }
    }

    private fun setupBillingRecyclerView() {
        billingAdapter = BillingRecyclerViewAdapter()
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingAdapter
            addItemDecoration(HorizontallItemDecoration())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun collectAddress() {
        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.INVISIBLE
                    }

                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Log.e("Error", "collectAddress: ${it.message}")
                    }

                    else -> Unit

                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Your order was placed", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Log.e("Error", "collectAddressOrder: ${it.message}")
                    }

                    else -> Unit
                }
            }
        }

        billingAdapter.differ.submitList(products)
        binding.tvTotalPrice.text = "$ $totalPrice"

        addressAdapter.onItemClick = {
            selectedAddress = it
            if (!navArgs.payment) {
                val b = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, b)
            }
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Snackbar.make(requireView(), "Please select an address", Snackbar.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            showInformationOrder()
        }
    }

    private fun showInformationOrder() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order Items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton("Yes") { dialog, _ ->
                val order =
                    Order(OrderStatus.Ordered.status, totalPrice, products, selectedAddress!!)
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun navigateToAddress() {
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment, null)
        }
    }
}