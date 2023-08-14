package com.alefmoreira.citytraveltracker.util.components.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.alefmoreira.citytraveltracker.databinding.DialogLoadingBinding

class AMLoadingDialog(private val context: Context) {
    private lateinit var binding: DialogLoadingBinding
    private val dialog = Dialog(context)


    fun show() {
        binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun hide() {
        dialog.dismiss()
    }
}