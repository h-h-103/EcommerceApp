package com.example.e_commerceapp.ui.fragmentsLogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerceapp.R
import com.example.e_commerceapp.databinding.FragmentSplashScreenBinding
import com.example.e_commerceapp.ui.activity.ShoppingActivity
import com.example.e_commerceapp.viewmodel.IntroductionSpViewModel
import com.example.e_commerceapp.viewmodel.IntroductionSpViewModel.Companion.ON_BOARDING_FRAGMENT
import com.example.e_commerceapp.viewmodel.IntroductionSpViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private val binding by lazy { FragmentSplashScreenBinding.inflate(layoutInflater) }
    private val viewModel: IntroductionSpViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNavigate()
        setupOnClick()
    }

    private fun collectNavigate() {
        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                when (it) {
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    ON_BOARDING_FRAGMENT -> {
                        findNavController().navigate(it)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupOnClick() {
        binding.btnFirstscreen.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_firstScreenFragment_to_secondScreenFragment)
        }
    }
}