package com.example.e_commerceapp.adapters.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter (
    fragment: FragmentManager,
    lifecycle: Lifecycle,
    private val categoriesFragments: List<Fragment>,
) : FragmentStateAdapter(fragment, lifecycle) {

    override fun getItemCount(): Int {
        return categoriesFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return categoriesFragments[position]
    }
}