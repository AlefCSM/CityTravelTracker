package com.alefmoreira.citytraveltracker.util.components.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.CardConfirmationBinding
import com.alefmoreira.citytraveltracker.util.DialogType

class AMConfirmationDialog(private val context: Context, private val type: DialogType) {
    private lateinit var binding: CardConfirmationBinding

    private val dialog = Dialog(context)
    var onConfirm: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    private lateinit var btnCancel: TextView
    private lateinit var btnExclude: TextView
    private lateinit var txtMsg: TextView

    var city: String = ""

    fun show() {

        binding = CardConfirmationBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        bind()
        val res = context.resources

        when (type) {
            DialogType.LEAVE -> {
                val msg = String.format(res.getString(R.string.exit_city))
                val exit = String.format(res.getString(R.string.exit))
                txtMsg.text = msg
                btnExclude.text = exit
            }
            DialogType.CONFIRMATION -> {
                val string = String.format(res.getString(R.string.exclude_city), city)
                txtMsg.text = htmlString(string)
            }
            DialogType.CUSTOM -> {
                TODO()
            }
        }

        setupClickListeners()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun setupClickListeners() {
        btnExclude.setOnClickListener {
            onConfirm?.invoke()
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            onCancel?.invoke()
            dialog.dismiss()
        }
    }

    private fun bind() {
        btnCancel = binding.btnCancel
        btnExclude = binding.btnExclude
        txtMsg = binding.txtMsg
    }

    private fun htmlString(string: String): CharSequence {
        return HtmlCompat.fromHtml(
            string,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).removeSuffix("\n")
    }


}