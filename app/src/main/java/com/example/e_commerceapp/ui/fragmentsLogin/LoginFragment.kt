@file:Suppress("DEPRECATION")

package com.example.e_commerceapp.ui.fragmentsLogin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerceapp.R
import com.example.e_commerceapp.databinding.FragmentLoginBinding
import com.example.e_commerceapp.dialog.setOnBottomSheetDialog
import com.example.e_commerceapp.ui.activity.ShoppingActivity
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val binding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateToRegisterFragment()
        setOnClickLogin()
        stateRegisterButton()
        forgotPassword()

        // Set up activity result launcher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                viewModel.handleGoogleSignInResult(task)
            }
        }

        binding.btnGoogle.setOnClickListener {
            viewModel.signInWithGoogle(activityResultLauncher)
        }
        observableGoogleLogin()
    }

    // Navigate to register fragment
    private fun navigateToRegisterFragment() {
        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    // When click on login button
    private fun setOnClickLogin() {
        binding.apply {
            btnLoginFragment.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.loginAccountWithEmailAndPassword(email, password)
            }
        }
    }

    // State click button
    private fun stateRegisterButton() {
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnLoginFragment.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnLoginFragment.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        binding.btnLoginFragment.revertAnimation()
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG)
                            .setAction("OK") {}
                            .show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun observableGoogleLogin() {
        lifecycleScope.launchWhenStarted {
            viewModel.googleLoginResult.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                       binding.btnLoginFragment.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnLoginFragment.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        binding.btnLoginFragment.revertAnimation()
                        Snackbar.make(requireView(), "Error: ${resource.message}", Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    // Forgot password
    private fun forgotPassword() {
        binding.tvForgotPassword.setOnClickListener {
            setOnBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Success Reset Send Link To Your Email",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit
                }
            }
        }
    }
}