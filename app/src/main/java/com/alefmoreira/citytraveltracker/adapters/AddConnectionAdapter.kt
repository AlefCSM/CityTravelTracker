package com.alefmoreira.citytraveltracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.data.Connection
import com.alefmoreira.citytraveltracker.databinding.AdapterFirstConnectionBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterLastConnectionBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterMiddleConnectionBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterSingleConnectionBinding
import com.alefmoreira.citytraveltracker.util.AdapterLayoutEnum
import javax.inject.Inject


class AddConnectionAdapter @Inject constructor(
    private val connections: List<Connection>,
//    private val onItemClick: ((AutocompletePrediction) -> Unit)? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (connections.size > 1) {
            when (connections[position]) {
                connections.first() -> AdapterLayoutEnum.FIRST.ordinal
                connections.last() -> AdapterLayoutEnum.LAST.ordinal
                else -> AdapterLayoutEnum.MIDDLE.ordinal
            }
        } else {
            AdapterLayoutEnum.SINGLE.ordinal
        }
    }

    class SingleItemViewHolder(
        private val binding: AdapterSingleConnectionBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(connection: Connection) {
            binding.txtConnectionName.text = connection.name
        }
    }

    class FirstItemViewHolder(
        private val binding: AdapterFirstConnectionBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(connection: Connection) {
            binding.txtConnectionName.text = connection.name
        }
    }

    class LastItemViewHolder(
        private val binding: AdapterLastConnectionBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(connection: Connection) {
            binding.txtConnectionName.text = connection.name
        }
    }

    class MiddleItemViewHolder(
        private val binding: AdapterMiddleConnectionBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(connection: Connection) {
            binding.txtConnectionName.text = connection.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return when (viewType) {
            AdapterLayoutEnum.SINGLE.ordinal -> {
                val binding = AdapterSingleConnectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SingleItemViewHolder(binding)
            }
            AdapterLayoutEnum.FIRST.ordinal -> {
                val binding = AdapterFirstConnectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FirstItemViewHolder(binding)
            }
            AdapterLayoutEnum.LAST.ordinal -> {
                val binding = AdapterLastConnectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LastItemViewHolder(binding)
            }
            else -> {
                val binding = AdapterMiddleConnectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MiddleItemViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val connection = connections[position]

        if (connections.size > 1) {
            when (connection) {
                connections.first() -> (holder as FirstItemViewHolder).bind(connection)
                connections.last() -> (holder as LastItemViewHolder).bind(connection)
                else -> (holder as MiddleItemViewHolder).bind(connection)
            }
        } else {
            (holder as SingleItemViewHolder).bind(connection)
        }


//        holder.itemView.setOnClickListener {
//            onItemClick?.invoke(connection)
//        }
    }

    override fun getItemCount(): Int {
        return connections.size
    }
}