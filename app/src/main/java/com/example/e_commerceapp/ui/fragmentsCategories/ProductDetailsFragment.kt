package com.example.e_commerceapp.ui.fragmentsCategories

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerceapp.R
import com.example.e_commerceapp.adapters.pager.ViewPager2Images
import com.example.e_commerceapp.adapters.recycler.ColorsAdapter
import com.example.e_commerceapp.adapters.recycler.SizeAdapter
import com.example.e_commerceapp.databinding.FragmentProductDetailsBinding
import com.example.e_commerceapp.model.CartProduct
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.util.hideBottomNav
import com.example.e_commerceapp.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private val binding by lazy { FragmentProductDetailsBinding.inflate(layoutInflater) }
    private val viewModel: DetailsViewModel by viewModels()
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var viewPagerAdapter: ViewPager2Images
    private lateinit var colorsAdapter: ColorsAdapter
    private lateinit var sizesAdapter: SizeAdapter
    private var selectedColor: Int? = null
    private var selectedSize: String? = null

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

        setupViewPager()
        setupColorsRv()
        setupSizesRv()
        setupData()

        binding.imgClose.setOnClickListener {
            findNavController().navigateUp()
        }

        addProductToCart()
        collectAddToCart()
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPager2Images()
        binding.viewpager2Images.adapter = viewPagerAdapter
    }

    private fun setupColorsRv() {
        colorsAdapter = ColorsAdapter()
        binding.rvColors.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = colorsAdapter
        }
    }

    private fun setupSizesRv() {
        sizesAdapter = SizeAdapter()
        binding.rvSizes.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sizesAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupData() {
        binding.apply {
            tvProductName.text = args.product.name
            tvProductPrice.text = "$ ${args.product.price}"
            tvProductDescription.text = args.product.description

            if (args.product.colors.isNullOrEmpty())
                tvColor.visibility = View.INVISIBLE
            if (args.product.sizes.isNullOrEmpty())
                tvSize.visibility = View.INVISIBLE
        }

        viewPagerAdapter.differ.submitList(args.product.images)
        args.product.colors?.let { colorsAdapter.differ.submitList(it) }
        args.product.sizes?.let { sizesAdapter.differ.submitList(it) }
    }

    private fun addProductToCart() {
        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(
                CartProduct(
                    args.product,
                    1,
                    selectedColor,
                    selectedSize
                )
            )
        }
    }

    private fun collectAddToCart() {
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnAddToCart.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnAddToCart.revertAnimation()
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.g_black))
                    }

                    is Resource.Error -> {
                        binding.btnAddToCart.stopAnimation()
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                    }

                    else -> Unit
                }
            }
        }
    }
}