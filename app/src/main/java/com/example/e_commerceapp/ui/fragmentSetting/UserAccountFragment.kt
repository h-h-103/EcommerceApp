package com.example.e_commerceapp.ui.fragmentSetting

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.e_commerceapp.databinding.FragmentUserAccountBinding
import com.example.e_commerceapp.dialog.setOnBottomSheetDialog
import com.example.e_commerceapp.model.User
import com.example.e_commerceapp.util.Resource
import com.example.e_commerceapp.viewmodel.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val binding by lazy { FragmentUserAccountBinding.inflate(layoutInflater) }
    private val viewModel: UserAccountViewModel by viewModels()
    private var imgUrl: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUser() // Ensure the user information is fetched

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it.data?.data?.let { imageUri ->
                    imgUrl = imageUri // Store image URI for saving later
                    Glide.with(this).load(imageUri).into(binding.imageUser)
                }
            }

        collectUser()
        updateImageUser()
        saveUserInformation()
        collectUpdateInfo()
        forgetPassword()
        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(requireView()).load(data.imgPath).error(ColorDrawable(Color.BLACK))
                .into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }

    private fun collectUser() {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> showUserLoading()
                    is Resource.Success -> {
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }

                    is Resource.Error -> {
                        hideUserLoading()
                        Log.e("Error", "collectUser: ${it.message}")
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun collectUpdateInfo() {
        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> binding.buttonSave.startAnimation()
                    is Resource.Success -> {
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        binding.buttonSave.revertAnimation()
                        Log.e("Error", "collectUser: ${it.message}")
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun saveUserInformation() {
        binding.buttonSave.setOnClickListener {
            val firstName = binding.edFirstName.text.toString().trim()
            val lastName = binding.edLastName.text.toString().trim()
            val email = binding.edEmail.text.toString().trim()
            val user = User(firstName, lastName, email)
            viewModel.updateUser(user, imgUrl)
        }
    }

    private fun updateImageUser() {
        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            activityResultLauncher.launch(intent)
        }
    }

    private fun forgetPassword() {
        binding.tvUpdatePassword.setOnClickListener {
            setOnBottomSheetDialog {}
        }
    }
}