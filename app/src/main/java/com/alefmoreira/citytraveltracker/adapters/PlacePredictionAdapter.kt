package com.alefmoreira.citytraveltracker.adapters

import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.trimmedLength
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.AdapterPlacePredictionBinding
import com.google.android.libraries.places.api.model.AutocompletePrediction
import javax.inject.Inject


class PlacePredictionAdapter @Inject constructor(
    private val predictionsD: List<AutocompletePrediction>,
    private val onItemClick: ((AutocompletePrediction) -> Unit)? = null,
    private val typedText: String
) : RecyclerView.Adapter<PlacePredictionAdapter.PlacePredictionViewHolder>() {
    class PlacePredictionViewHolder(
        private val binding: AdapterPlacePredictionBinding,
        private val resources: Resources
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(prediction: AutocompletePrediction, typedText: String) {

            val span = SpannableString(prediction.getPrimaryText(null).toString())
            val color = ResourcesCompat.getColor(resources, R.color.dark_2, null)
            span.setSpan(
                ForegroundColorSpan(color),
                0,
                typedText.trimmedLength(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )

            binding.txtPlaceName.setText(span, TextView.BufferType.SPANNABLE)
            binding.txtPlaceCountry.text = prediction.getSecondaryText(null).toString()
        }


    }


//    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
//        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
//            return oldItem == newItem
//        }
//
//        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    private val differ = AsyncListDiffer(this, diffCallback)
//
//    var predictions: List<String>
//        get() = differ.currentList
//        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {
        val binding = AdapterPlacePredictionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlacePredictionViewHolder(binding, parent.resources)
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {
        val prediction = predictionsD[position]
        holder.bind(prediction, typedText)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(prediction)
        }
    }

    override fun getItemCount(): Int {
        return predictionsD.size
    }
}