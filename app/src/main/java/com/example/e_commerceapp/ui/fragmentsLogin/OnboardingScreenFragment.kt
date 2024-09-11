package com.example.e_commerceapp.ui.fragmentsLogin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerceapp.R
import com.example.e_commerceapp.databinding.FragmentOnboardingScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingScreenFragment : Fragment() {

    private val binding by lazy { FragmentOnboardingScreenBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            binding.btnSplashRegister.setOnClickListener {
                findNavController().navigate(R.id.action_secondScreenFragment_to_registerFragment)
            }
            btnSplashSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_secondScreenFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().finish()
    }
}