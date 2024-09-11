package com.example.e_commerceapp.ui.fragmentsLogin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerceapp.R
import com.example.e_commerceapp.databinding.FragmentRegisterBinding
import com.example.e_commerceapp.model.User
import com.example.e_commerceapp.util.RegisterValidation
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val binding by lazy { FragmentRegisterBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        setOnClickRegister()
        stateRegisterButton()
        stateValidation()

    }

    // When click on login button
    private fun setOnClickRegister() {
        binding.apply {
            btnLogin.setOnClickListener {
                val user = User(
                    firstName = edFirstName.text.toString().trim(),
                    lastName = edLastName.text.toString().trim(),
                    email = edEmail.text.toString().trim()
                )
                val password = edPassword.text.toString()
                viewModel.createAccountWithUserAndPassword(user, password)
            }
        }
    }

    // State click button
    private fun stateRegisterButton() {
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnLogin.revertAnimation()
                        Log.d(TAG, "onSuccess: ${it.data}")
                    }

                    is Resource.Error -> {
                        binding.btnLogin.revertAnimation()
                        Log.d(TAG, "onError: ${it.message}")
                    }

                    else -> Unit
                }
            }
        }
    }

    // State validation
    private fun stateValidation() {
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPassword.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}