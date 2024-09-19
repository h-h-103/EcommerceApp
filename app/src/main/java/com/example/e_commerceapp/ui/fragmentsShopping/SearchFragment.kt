package com.example.e_commerceapp.ui.fragmentsShopping

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerceapp.R
import com.example.e_commerceapp.adapters.recycler.SearchRvAdapter
import com.example.e_commerceapp.databinding.FragmentSearchBinding
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val binding by lazy { FragmentSearchBinding.inflate(layoutInflater) }
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchRv()
        searchProducts()
        setOnClickItem()
    }

    private fun setupSearchRv() {
        searchAdapter = SearchRvAdapter()
        binding.rvSearch.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
        }
    }

    private fun searchProducts() {
        var job: Job? = null
        binding.edSearch.addTextChangedListener { searchQuery ->
            job?.cancel()
            job = lifecycleScope.launchWhenStarted {
                delay(500) // Delay for debouncing input
                val query = searchQuery.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchProducts(query) // Use trimmed query to avoid unnecessary spaces
                    collectSearchProducts()
                } else {
                    // If the query is empty, we can clear the list or perform any other necessary action
                    searchAdapter.differ.submitList(emptyList()) // Clear the list when query is empty
                }
            }
        }
    }

    private fun collectSearchProducts() {
        lifecycleScope.launchWhenStarted {
            viewModel.search.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.shimmerSearch.visibility = View.VISIBLE
                        binding.rvSearch.visibility = View.GONE
                        binding.shimmerSearch.startShimmer()
                    }

                    is Resource.Success -> {
                        binding.shimmerSearch.stopShimmer()
                        binding.shimmerSearch.visibility = View.GONE
                        binding.rvSearch.visibility = View.VISIBLE
                        resource.data?.let { products ->
                            searchAdapter.differ.submitList(products)
                        }
                    }

                    is Resource.Error -> {
                        binding.progressbarCategories.visibility = View.GONE
                        Log.e("Error", "collectSearchProducts: ${resource.message}")
                    }

                    else -> Unit
                }
            }
        }
    }


    private fun setOnClickItem() {
        searchAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment, bundle)
        }
    }
}