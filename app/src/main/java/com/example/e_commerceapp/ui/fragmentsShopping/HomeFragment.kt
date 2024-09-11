package com.example.e_commerceapp.ui.fragmentsShopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_commerceapp.adapters.pager.HomeViewPagerAdapter
import com.example.e_commerceapp.databinding.FragmentHomeBinding
import com.example.e_commerceapp.ui.fragmentsCategories.ChairFragment
import com.example.e_commerceapp.ui.fragmentsCategories.MainFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import com.example.e_commerceapp.ui.fragmentsCategories.TableFragment as TableFragment1

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachViewPager()
    }

    // Attach ViewPager to TabLayout
    private fun attachViewPager() {
        val categoriesFragments = arrayListOf(
            MainFragment(),
            ChairFragment(),
            TableFragment1(),
        )

        binding.viewpagerHome.isUserInputEnabled = false

        val homeViewPagerAdapter = HomeViewPagerAdapter(childFragmentManager, lifecycle, categoriesFragments)
        binding.viewpagerHome.adapter = homeViewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Table"
            }
        }.attach()
    }
}