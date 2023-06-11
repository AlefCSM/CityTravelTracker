package com.alefmoreira.citytraveltracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.databinding.AdapterPlacePredictionBinding
import com.google.android.libraries.places.api.model.AutocompletePrediction
import javax.inject.Inject


class PlacePredictionAdapter @Inject constructor(
    private val predictionsD: List<AutocompletePrediction>
) : RecyclerView.Adapter<PlacePredictionAdapter.PlacePredictionViewHolder>() {
    class PlacePredictionViewHolder(binding: AdapterPlacePredictionBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var predictions: List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {

        val binding = AdapterPlacePredictionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlacePredictionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {
        val prediction = predictionsD[position]
        holder.itemView.apply {

        }
    }

    override fun getItemCount(): Int {
        return predictionsD.size
    }
}