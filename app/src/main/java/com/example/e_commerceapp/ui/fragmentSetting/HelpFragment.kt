package com.example.e_commerceapp.ui.fragmentSetting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerceapp.databinding.FragmentHelpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelpFragment : Fragment() {

    private val binding by lazy { FragmentHelpBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go Email
        binding.imgEmail.setOnClickListener {
            val actionSend = Intent(Intent.ACTION_SENDTO)   // To send email
            actionSend.data = Uri.parse("mailto:")
            actionSend.putExtra(Intent.EXTRA_EMAIL, arrayOf("husseinCEO10@gmail.com"))
            actionSend.putExtra(Intent.EXTRA_SUBJECT, "Help Us")
            actionSend.putExtra(Intent.EXTRA_TEXT, "Hi")
            startActivity(actionSend)
        }

        // Go to call direct
        binding.imgPhone.setOnClickListener {
            val actionCall = Intent(Intent.ACTION_DIAL)   // To call phone
            actionCall.data = Uri.parse("tel:07740302222")   // To specific number
            startActivity(actionCall)
        }

        binding.imgCloseHelp.setOnClickListener { findNavController().navigateUp() }

    }
}