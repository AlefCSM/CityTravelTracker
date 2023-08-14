package com.alefmoreira.citytraveltracker.util.components.adapters

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.data.Connection
import com.alefmoreira.citytraveltracker.databinding.AdapterRouteConnectionBinding

class RouteConnectionAdapter :
    RecyclerView.Adapter<RouteConnectionAdapter.RouteConnectionViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Connection>() {
        override fun areItemsTheSame(oldItem: Connection, newItem: Connection): Boolean =
            oldItem === newItem


        override fun areContentsTheSame(oldItem: Connection, newItem: Connection): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.name == newItem.name &&
                    oldItem.cityId == newItem.cityId &&
                    oldItem.placeId == newItem.placeId
        }
    }


    private val differ = AsyncListDiffer(this, diffCallback)

    var connections: List<Connection>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    class RouteConnectionViewHolder(private val binding: AdapterRouteConnectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(connection: Connection) {
            binding.txtConnection.text = connection.name
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteConnectionViewHolder {

        val binding = AdapterRouteConnectionBinding.inflate(from(parent.context), parent, false)

        return RouteConnectionViewHolder(binding)

    }

    override fun getItemCount(): Int = connections.size

    override fun onBindViewHolder(holder: RouteConnectionViewHolder, position: Int) {
        val connection = connections[position]
        holder.bind(connection)
    }
}