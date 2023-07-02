package com.alefmoreira.citytraveltracker.util.components.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.data.Connection
import com.alefmoreira.citytraveltracker.databinding.AdapterFirstConnectionBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterLastConnectionBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterMiddleConnectionBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterSingleConnectionBinding
import com.alefmoreira.citytraveltracker.util.AdapterLayoutEnum

class AddConnectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<Connection>() {
        override fun areItemsTheSame(oldItem: Connection, newItem: Connection): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Connection, newItem: Connection): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.cityId == newItem.cityId &&
                    oldItem.name == newItem.name &&
                    oldItem.placeId == newItem.placeId
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var connections: List<Connection>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    var onItemClick: ((Connection) -> Unit)? = null
    var onDeleteClick: ((Connection) -> Unit)? = null

    class SingleItemViewHolder(
        private val binding: AdapterSingleConnectionBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(connection: Connection, onDelete: (() -> Unit)?) {
            binding.txtConnectionName.text = connection.name
            binding.btnRemoveConnection.setOnClickListener {
                onDelete?.invoke()
            }
        }
    }

    class FirstItemViewHolder(
        private val binding: AdapterFirstConnectionBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(connection: Connection, onDelete: (() -> Unit)?) {
            binding.txtConnectionName.text = connection.name
            binding.btnRemoveConnection.setOnClickListener {
                onDelete?.invoke()
            }
        }
    }

    class MiddleItemViewHolder(
        private val binding: AdapterMiddleConnectionBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(connection: Connection, onDelete: (() -> Unit)?) {
            binding.txtConnectionName.text = connection.name
            binding.btnRemoveConnection.setOnClickListener {
                onDelete?.invoke()
            }
        }
    }

    class LastItemViewHolder(
        private val binding: AdapterLastConnectionBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(connection: Connection, onDelete: (() -> Unit)?) {
            binding.txtConnectionName.text = connection.name
            binding.btnRemoveConnection.setOnClickListener {
                onDelete?.invoke()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (connections.size > 1) {
            if (connections[position] === connections[0]) {
                AdapterLayoutEnum.FIRST.ordinal
            } else if (connections[position] === connections[connections.size - 1]) {
                AdapterLayoutEnum.LAST.ordinal
            } else {
                AdapterLayoutEnum.MIDDLE.ordinal
            }
        } else {
            AdapterLayoutEnum.SINGLE.ordinal
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

        when (getItemViewType(position)) {
            AdapterLayoutEnum.SINGLE.ordinal -> (holder as SingleItemViewHolder).bind(
                connection,
                onDelete = {
                    notifyDeletion(position)
                    onDeleteClick?.invoke(connection)
                }
            )
            AdapterLayoutEnum.FIRST.ordinal -> (holder as FirstItemViewHolder).bind(
                connection,
                onDelete = {
                    notifyDeletion(position)
                    onDeleteClick?.invoke(connection)
                }
            )
            AdapterLayoutEnum.LAST.ordinal -> (holder as LastItemViewHolder).bind(
                connection,
                onDelete = {
                    notifyDeletion(position)
                    onDeleteClick?.invoke(connection)
                }
            )
            else -> {
                (holder as MiddleItemViewHolder).bind(connection, onDelete = {
                    notifyDeletion(position)
                    onDeleteClick?.invoke(connection)
                })
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(connection)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyDeletion(position: Int) {
        connections.drop(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return connections.size
    }
}