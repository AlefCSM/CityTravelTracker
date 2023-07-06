package com.alefmoreira.citytraveltracker.util.components.adapters


import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.AdapterFirstRouteBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterLastRouteBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterMiddleRouteBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterSingleRouteBinding
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.util.AdapterLayoutEnum

class RouteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<Route>() {
        override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
            return oldItem.city.id == newItem.city.id &&
                    oldItem.city.name == newItem.city.name &&
                    oldItem.city.placeId == newItem.city.placeId
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var routes: List<Route>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    var onItemClick: ((Route) -> Unit)? = null

    class SingleItemViewHolder(
        private val binding: AdapterSingleRouteBinding,
        private val res: Resources
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var isExpanded = false
        fun bind(route: Route) {
            binding.txtCityName.text = route.city.name
            if (route.connections.isNotEmpty()) {
                binding.connectionsLayout.visibility = View.VISIBLE
                binding.showConnections.setOnClickListener {
                    if (isExpanded) {
                        binding.showConnections.text =
                            String.format(res.getString(R.string.hide_connections))
                        binding.connectionsList.visibility = View.VISIBLE
                    } else {
                        binding.showConnections.text =
                            String.format(res.getString(R.string.show_connections))
                        binding.connectionsList.visibility = View.GONE
                    }
                    isExpanded = !isExpanded
                }
            }
        }
    }

    class FirstItemViewHolder(
        private val binding: AdapterFirstRouteBinding,
        private val res: Resources
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private var isExpanded = false
        fun bind(route: Route) {
            binding.txtCityName.text = route.city.name
            if (route.connections.isNotEmpty()) {
                binding.connectionsLayout.visibility = View.VISIBLE
                binding.showConnections.setOnClickListener {
                    if (isExpanded) {
                        binding.showConnections.text =
                            String.format(res.getString(R.string.hide_connections))
                        binding.connectionsList.visibility = View.VISIBLE
                    } else {
                        binding.showConnections.text =
                            String.format(res.getString(R.string.show_connections))
                        binding.connectionsList.visibility = View.GONE
                    }
                    isExpanded = !isExpanded
                }
            }
        }
    }

    class MiddleItemViewHolder(
        private val binding: AdapterMiddleRouteBinding,
        private val res: Resources
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private var isExpanded = false
        fun bind(route: Route) {
            binding.txtCityName.text = route.city.name
            if (route.connections.isNotEmpty()) {
                binding.connectionsLayout.visibility = View.VISIBLE
                binding.showConnections.setOnClickListener {
                    isExpanded = !isExpanded
                    if (isExpanded) {
                        binding.showConnections.text =
                            String.format(res.getString(R.string.hide_connections))
                        binding.connectionsList.visibility = View.VISIBLE
                    } else {
                        binding.showConnections.text =
                            String.format(res.getString(R.string.show_connections))
                        binding.connectionsList.visibility = View.GONE
                    }
                }
            }
        }
    }

    class LastItemViewHolder(
        private val binding: AdapterLastRouteBinding,
        private val res: Resources
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var isExpanded = false
        fun bind(route: Route) {
            binding.txtCityName.text = route.city.name
            if (route.connections.isNotEmpty()) {
                binding.connectionsLayout.visibility = View.VISIBLE
                binding.showConnections.setOnClickListener {
                    if (isExpanded) {
                        binding.showConnections.text =
                            String.format(res.getString(R.string.hide_connections))
                        binding.connectionsList.visibility = View.VISIBLE
                    } else {
                        binding.showConnections.text =
                            String.format(res.getString(R.string.show_connections))
                        binding.connectionsList.visibility = View.GONE
                    }
                    isExpanded = !isExpanded
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (routes.size > 1) {
            if (routes[position] === routes[0]) {
                AdapterLayoutEnum.FIRST.ordinal
            } else if (routes[position] === routes[routes.size - 1]) {
                AdapterLayoutEnum.LAST.ordinal
            } else {
                AdapterLayoutEnum.MIDDLE.ordinal
            }
        } else {
            AdapterLayoutEnum.SINGLE.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val res = parent.resources
        return when (viewType) {
            AdapterLayoutEnum.SINGLE.ordinal -> {
                val binding = AdapterSingleRouteBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                SingleItemViewHolder(binding, res)
            }
            AdapterLayoutEnum.FIRST.ordinal -> {
                val binding = AdapterFirstRouteBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                FirstItemViewHolder(binding, res)
            }
            AdapterLayoutEnum.LAST.ordinal -> {
                val binding = AdapterLastRouteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LastItemViewHolder(binding, res)
            }
            else -> {
                val binding = AdapterMiddleRouteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MiddleItemViewHolder(binding, res)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val route = routes[position]

        when (getItemViewType(position)) {
            AdapterLayoutEnum.SINGLE.ordinal -> (holder as SingleItemViewHolder).bind(
                route
            )
            AdapterLayoutEnum.FIRST.ordinal -> (holder as FirstItemViewHolder).bind(
                route
            )
            AdapterLayoutEnum.LAST.ordinal -> (holder as LastItemViewHolder).bind(
                route
            )
            else -> {
                (holder as MiddleItemViewHolder).bind(route)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(route)
        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }
}