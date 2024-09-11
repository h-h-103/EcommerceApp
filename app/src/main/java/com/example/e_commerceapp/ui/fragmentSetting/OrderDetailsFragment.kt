package com.example.e_commerceapp.ui.fragmentSetting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerceapp.adapters.recycler.BillingRecyclerViewAdapter
import com.example.e_commerceapp.databinding.FragmentOrderDetailsBinding
import com.example.e_commerceapp.model.order.OrderStatus
import com.example.e_commerceapp.model.order.getOrderStatus
import com.example.e_commerceapp.util.VerticalItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {

    private val binding by lazy { FragmentOrderDetailsBinding.inflate(layoutInflater) }
    private val navArgs by navArgs<OrderDetailsFragmentArgs>()
    private lateinit var billingRecyclerViewAdapter: BillingRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBillingRecyclerView()

        binding.apply {
            tvOrderId.text = "Order #${navArgs.order.orderId}"
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )
            val currentOrderState = when (getOrderStatus(navArgs.order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }
            stepView.go(currentOrderState, false)
            if (currentOrderState == 3) {
                stepView.done(true)
            }
            tvFullName.text = navArgs.order.address.fullName
            tvAddress.text = "${navArgs.order.address.street} ${navArgs.order.address.city}"
            tvPhoneNumber.text = navArgs.order.address.phone
            tvTotalprice.text = "$ ${navArgs.order.totalPrice}"
            billingRecyclerViewAdapter.differ.submitList(navArgs.order.products)
        }

        binding.imgCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupBillingRecyclerView() {
        billingRecyclerViewAdapter = BillingRecyclerViewAdapter()
        binding.rvProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = billingRecyclerViewAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}