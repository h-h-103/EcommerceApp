package com.example.e_commerceapp.ui.fragmentSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.e_commerceapp.databinding.FragmentAddressBinding
import com.example.e_commerceapp.model.Address
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.viewmodel.AddressViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddressFragment : Fragment() {

    private val binding by lazy { FragmentAddressBinding.inflate(layoutInflater) }
    private val navArgs by navArgs<AddressFragmentArgs>()
    private val viewModel: AddressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navArgs.address == null) {
            binding.btnDelete.visibility = View.GONE
        } else {
            binding.apply {
                edAddressTitle.setText(navArgs.address?.addressTitle)
                edFullName.setText(navArgs.address?.fullName)
                edStreet.setText(navArgs.address?.street)
                edPhone.setText(navArgs.address?.phone)
                edCity.setText(navArgs.address?.city)
                edState.setText(navArgs.address?.state)
            }
        }
        addAddress()
        collectAddressAndError()
        binding.imgAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun addAddress() {
        binding.apply {
            btnAddNewAddress.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString().trim()
                val fullName = edFullName.text.toString().trim()
                val street = edStreet.text.toString().trim()
                val phone = edPhone.text.toString().trim()
                val city = edCity.text.toString().trim()
                val state = edState.text.toString().trim()
                viewModel.addAddress(Address(addressTitle, fullName, street, phone, city, state))
            }
        }
    }

    private fun collectAddressAndError() {
        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}