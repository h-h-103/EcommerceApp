package com.example.e_commerceapp.dialog

import android.annotation.SuppressLint
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.e_commerceapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("InflateParams")
fun Fragment.setOnBottomSheetDialog(onSendClick: (String) -> Unit) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.fragment_reset_password, null)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.setContentView(view)
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edEmail)
    val btnCancel = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_cancel)
    val btnSend = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_send)

    btnSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}