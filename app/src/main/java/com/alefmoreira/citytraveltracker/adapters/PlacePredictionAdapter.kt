package com.alefmoreira.citytraveltracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.R
import com.google.android.libraries.places.api.model.AutocompletePrediction
import javax.inject.Inject


class PlacePredictionAdapter @Inject constructor(
    private val predictionsD: List<AutocompletePrediction>
) : RecyclerView.Adapter<PlacePredictionAdapter.PlaceViewHolder>() {
    class PlaceViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val txtPlaceName: TextView

        init {
            txtPlaceName = itemview.findViewById(R.id.txt_place_name)
            Adap
        }
    }


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_place, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val prediction = predictionsD[position]
        holder.itemView.apply {

        }
    }

    override fun getItemCount(): Int {
        return predictionsD.size
    }
}