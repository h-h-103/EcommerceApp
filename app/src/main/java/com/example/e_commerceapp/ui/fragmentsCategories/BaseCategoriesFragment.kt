package com.example.e_commerceapp.ui.fragmentsCategories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerceapp.R
import com.example.e_commerceapp.adapters.recycler.ProductsRecyclerAdapter
import com.example.e_commerceapp.databinding.FragmentBaseCategoriesBinding
import com.example.e_commerceapp.util.HorizontallItemDecoration
import com.example.e_commerceapp.util.VerticalItemDecoration
import com.example.e_commerceapp.util.hideBottomNav
import com.example.e_commerceapp.util.showBottomNav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseCategoriesFragment : Fragment() {

    private val binding by lazy { FragmentBaseCategoriesBinding.inflate(layoutInflater) }
    protected val offerAdapter: ProductsRecyclerAdapter by lazy { ProductsRecyclerAdapter() }
    protected val bestAdapter: ProductsRecyclerAdapter by lazy { ProductsRecyclerAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBesetRv()
        setOnClickItem()

        binding.rvAds.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    onOfferPagingRequest()
                }
            }
        })

        binding.scrollChair.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= (v.height + scrollY)) {
                onBestProductsPagingRequest()
            }
        })
    }

    open fun onOfferPagingRequest() {}

    open fun onBestProductsPagingRequest() {}

    fun showLoading() {
        binding.progressbar2.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.progressbar2.visibility = View.GONE
    }

    private fun setupOfferRv() {
        binding.rvAds.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
            addItemDecoration(HorizontallItemDecoration())
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun setupBesetRv() {
        binding.rvBestDeals.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestAdapter
            addItemDecoration(HorizontallItemDecoration())
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun setOnClickItem() {
        offerAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

    }

    override fun onResume() {
        super.onResume()
        showBottomNav()
    }
}