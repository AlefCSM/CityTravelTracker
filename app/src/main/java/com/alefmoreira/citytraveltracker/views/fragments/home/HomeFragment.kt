package com.alefmoreira.citytraveltracker.views.fragments.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.FragmentHomeBinding
import com.alefmoreira.citytraveltracker.util.components.AMAnimator
import com.alefmoreira.citytraveltracker.util.components.adapters.RouteAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    val viewModel: HomeViewModel by activityViewModels()

    private lateinit var txtMileage: TextView
    private lateinit var txtHours: TextView
    private lateinit var layoutRoutes: LinearLayoutCompat
    private lateinit var layoutNoRoutes: LinearLayoutCompat
    private lateinit var routeRecyclerView: RecyclerView
    private lateinit var routeAdapter: RouteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        bind()
        val btnStart = binding.btnStart

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getRoutes()
                setupSubscription()
            }
        }


        btnStart.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRouteFragment())
        }

    }

    private fun bind() {
        txtMileage = binding.txtMileage
        txtHours = binding.txtHours
        layoutRoutes = binding.layoutRoutes
        layoutNoRoutes = binding.layoutNoRoutes
        routeRecyclerView = binding.routeList
    }

    private suspend fun setupSubscription() {
        viewModel.routes.collect {
            it.peekContent().data?.let { list ->
                routeAdapter = RouteAdapter()
                routeAdapter.routes = list

                routeRecyclerView.apply {
                    adapter = routeAdapter
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    val animator = AMAnimator(context)
                    itemAnimator = animator
                }
            }

        }
    }
}