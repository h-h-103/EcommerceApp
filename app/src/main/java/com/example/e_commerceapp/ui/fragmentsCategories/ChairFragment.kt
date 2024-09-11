package com.example.e_commerceapp.ui.fragmentsCategories

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_commerceapp.model.Category
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.viewmodel.CategoryViewModel
import com.example.e_commerceapp.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ChairFragment : BaseCategoriesFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore
    private val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Chair)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectOfferProducts()
        collectBestProducts()
    }

    private fun collectOfferProducts() {
        lifecycleScope.launchWhenResumed {
            viewModel.offerProducts.collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideLoading()
                        offerAdapter.differ.submitList(resource.data)
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e("Error", resource.message.toString())
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun collectBestProducts() {
        lifecycleScope.launchWhenResumed {
            viewModel.bestProducts.collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideLoading()
                        bestAdapter.differ.submitList(resource.data)
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e("Error", resource.message.toString())
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onOfferPagingRequest() {
        super.onOfferPagingRequest()
    }

    override fun onBestProductsPagingRequest() {
        super.onBestProductsPagingRequest()
    }
}