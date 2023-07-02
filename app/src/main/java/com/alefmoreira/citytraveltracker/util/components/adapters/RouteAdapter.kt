package com.alefmoreira.citytraveltracker.util.components.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.databinding.AdapterFirstRouteBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterLastRouteBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterMiddleRouteBinding
import com.alefmoreira.citytraveltracker.databinding.AdapterSingleRouteBinding
import com.alefmoreira.citytraveltracker.model.Route
import com.alefmoreira.citytraveltracker.util.AdapterLayoutEnum
import javax.inject.Inject


class RouteAdapter @Inject constructor(
    private val routes: List<Route>,
//    private val onItemClick: ((Connection) -> Unit)? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (routes.size > 1) {
            when (routes[position]) {
                routes.first() -> AdapterLayoutEnum.FIRST.ordinal
                routes.last() -> AdapterLayoutEnum.LAST.ordinal
                else -> AdapterLayoutEnum.MIDDLE.ordinal
            }
        } else {
            AdapterLayoutEnum.SINGLE.ordinal
        }
    }

    class SingleItemViewHolder(
        private val binding: AdapterSingleRouteBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(route: Route) {
            binding.txtConnectionName.text = route.city.name
        }
    }

    class FirstItemViewHolder(
        private val binding: AdapterFirstRouteBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(route: Route) {
            binding.txtConnectionName.text = route.city.name
        }
    }

    class LastItemViewHolder(
        private val binding: AdapterLastRouteBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(route: Route) {
            binding.txtConnectionName.text = route.city.name
        }
    }

    class MiddleItemViewHolder(
        private val binding: AdapterMiddleRouteBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(route: Route) {
            binding.txtConnectionName.text = route.city.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return when (viewType) {
            AdapterLayoutEnum.SINGLE.ordinal -> {
                val binding = AdapterSingleRouteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SingleItemViewHolder(binding)
            }
            AdapterLayoutEnum.FIRST.ordinal -> {
                val binding = AdapterFirstRouteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FirstItemViewHolder(binding)
            }
            AdapterLayoutEnum.LAST.ordinal -> {
                val binding = AdapterLastRouteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LastItemViewHolder(binding)
            }
            else -> {
                val binding = AdapterMiddleRouteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MiddleItemViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val route = routes[position]

        if (routes.size > 1) {
            when (route) {
                routes.first() -> (holder as FirstItemViewHolder).bind(route)
                routes.last() -> (holder as LastItemViewHolder).bind(route)
                else -> (holder as MiddleItemViewHolder).bind(route)
            }
        } else {
            (holder as SingleItemViewHolder).bind(route)
        }


//        holder.itemView.setOnClickListener {
//            onItemClick?.invoke(connection)
//        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }
}