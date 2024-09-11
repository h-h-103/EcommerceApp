package com.example.e_commerceapp.ui.fragmentsCategories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerceapp.R
import com.example.e_commerceapp.adapters.recycler.BestDealsRecyclerAdapter
import com.example.e_commerceapp.adapters.recycler.ProductsRecyclerAdapter
import com.example.e_commerceapp.adapters.recycler.SpecialProductRecyclerAdapter
import com.example.e_commerceapp.databinding.FragmentMainBinding
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.util.showBottomNav
import com.example.e_commerceapp.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainFragment : Fragment() {

    private val binding by lazy { FragmentMainBinding.inflate(layoutInflater) }
    private val viewModel: MainCategoryViewModel by viewModels()
    private lateinit var specialProductAdapter: SpecialProductRecyclerAdapter
    private lateinit var bestDealsAdapter: BestDealsRecyclerAdapter
    private lateinit var bestProductsAdapter: ProductsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialRv()
        collectSpecialProducts()

        setupBestDeals()
        collectBestDeals()

        setupBestProducts()
        collectBestProducts()

        setOnClickItem()
    }

    private fun showLoading() {
        binding.progressbar2.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressbar2.visibility = View.GONE
    }

    // Setup the special product recycler view
    private fun setupSpecialRv() {
        specialProductAdapter = SpecialProductRecyclerAdapter()
        binding.rvAds.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductAdapter

        }
    }

    // Collect the special products from the view model
    private fun collectSpecialProducts() {
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collect {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        specialProductAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e("Error", it.message.toString())
                    }

                    else -> Unit
                }
            }
        }
    }

    // Setup the best deals recycler view
    private fun setupBestDeals() {
        bestDealsAdapter = BestDealsRecyclerAdapter()
        binding.rvBestDeals.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    // Collect the best deals from the view model
    private fun collectBestDeals() {
        lifecycleScope.launchWhenStarted {
            viewModel.bestDeals.collect {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        bestDealsAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e("Error", it.message.toString())
                    }

                    else -> Unit
                }
            }
        }
    }

    // Setup the best products recycler view
    private fun setupBestProducts() {
        bestProductsAdapter = ProductsRecyclerAdapter()
        binding.rvChairs.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    // Collect the best products from the view model
    private fun collectBestProducts() {
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbar2.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbar2.visibility = View.GONE
                        bestProductsAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        binding.progressbar2.visibility = View.GONE
                        Log.e("Error", it.message.toString())
                    }

                    else -> Unit
                }
            }
        }

        binding.scrollChair.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= (v.height + scrollY)) {
                viewModel.fetchBestProduct()
            }
        })
    }

    private fun setOnClickItem() {
        specialProductAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestDealsAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestProductsAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNav()
    }
}