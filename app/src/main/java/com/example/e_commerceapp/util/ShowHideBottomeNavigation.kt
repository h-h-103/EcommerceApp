package com.example.e_commerceapp.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.e_commerceapp.R
import com.example.e_commerceapp.ui.activity.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNav() {
    val bottomNavigationView = (activity as ShoppingActivity)
        .findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNav() {
    val bottomNavigationView = (activity as ShoppingActivity)
        .findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.visibility = View.VISIBLE
}