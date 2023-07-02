package com.alefmoreira.citytraveltracker.util.components.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.alefmoreira.citytraveltracker.databinding.CardAlertBinding

class AMAlertDialog(private val context: Context) {
    private lateinit var binding: CardAlertBinding

    private val dialog = Dialog(context)
    var onConfirm: (() -> Unit)? = null

    private lateinit var btnOk: Button
    private lateinit var txtTitle: TextView
    private lateinit var txtMsg: TextView

    var title: String = ""
    var message: String = ""

    fun show() {
        binding = CardAlertBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        bind()
        setupText()
        setupClickListeners()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun setupClickListeners() {
        btnOk.setOnClickListener {
            onConfirm?.invoke()
            dialog.dismiss()
        }
    }

    private fun setupText() {
        if (title.isNotEmpty()) {
            txtTitle.text = title
        }
        if (message.isNotEmpty()) {
            txtMsg.text = message
        }
    }

    private fun bind() {
        txtTitle = binding.txtTitle
        txtMsg = binding.txtMsg
        btnOk = binding.btnOk
    }
}