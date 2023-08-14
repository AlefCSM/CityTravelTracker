package com.alefmoreira.citytraveltracker.util.components.adapters

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.trimmedLength
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.AdapterPlacePredictionBinding
import com.alefmoreira.citytraveltracker.util.Utils
import com.google.android.libraries.places.api.model.AutocompletePrediction
import javax.inject.Inject


class PlacePredictionAdapter @Inject constructor(
) : RecyclerView.Adapter<PlacePredictionAdapter.PlacePredictionViewHolder>() {
    class PlacePredictionViewHolder(
        private val binding: AdapterPlacePredictionBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(prediction: AutocompletePrediction, typedText: String) {

            val span = SpannableString(prediction.getPrimaryText(null).toString())
            val color = getColor(binding.root.context)

            if ((typedText.trimmedLength() > span.length).not()) {
                span.setSpan(
                    ForegroundColorSpan(color),
                    0,
                    typedText.trimmedLength(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
            binding.txtPlaceName.setText(span, TextView.BufferType.SPANNABLE)
            binding.txtPlaceCountry.text = prediction.getSecondaryText(null).toString()
        }

        private fun getColor(context: Context): Int {
            return if (Utils.isDarkModeOn(context)) {
                ResourcesCompat.getColor(context.resources, R.color.secondary_1, null)
            } else {
                ResourcesCompat.getColor(context.resources, R.color.dark_2, null)
            }
        }
    }


    private val diffCallback = object : DiffUtil.ItemCallback<AutocompletePrediction>() {
        override fun areItemsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem.getPrimaryText(null) == newItem.getPrimaryText(null) &&
                    oldItem.getSecondaryText(null) == newItem.getSecondaryText(null) &&
                    oldItem.placeId == newItem.placeId

        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var predictions: List<AutocompletePrediction>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    var onItemClick: ((AutocompletePrediction) -> Unit)? = null
    var typedText: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {
        val binding = AdapterPlacePredictionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlacePredictionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {
        val prediction = predictions[position]
        holder.bind(prediction, typedText)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(prediction)
        }
    }

    override fun getItemCount(): Int {
        return predictions.size
    }
}