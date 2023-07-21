package com.alefmoreira.citytraveltracker.views.fragments.home

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.FragmentHomeBinding
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.util.components.AMAnimator
import com.alefmoreira.citytraveltracker.util.components.adapters.RouteAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var routeAdapter: RouteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        val btnStart = binding.btnStart
        btnStart.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRouteFragment())
        }

//        if(!viewModel.hasAnimated) {
//            viewModel.hasAnimated = true
        val animation = AnimationUtils.loadAnimation(context, R.anim.presenting)
        binding.metricLayout.startAnimation(animation)
//        }

        viewModel.getRoutes()
        setupSubscriptions()
    }

    private fun routeSubscription() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.test.collect { list ->
                if (list.isEmpty()) {
                    binding.layoutNoRoutes.visibility = View.VISIBLE
                    binding.layoutRoutes.visibility = View.GONE
                } else {
                    binding.layoutNoRoutes.visibility = View.GONE
                    binding.layoutRoutes.visibility = View.VISIBLE


                    routeAdapter = RouteAdapter()
                    routeAdapter.apply {
                        routes = list
                        onItemClick = { route ->
                            route.city.id?.let { id ->
                                findNavController().navigate(
                                    (HomeFragmentDirections.actionHomeFragmentToRouteFragment(
                                        id
                                    ))
                                )
                            }
                        }
                    }


                    binding.routeList.apply {
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

    private fun networkSubscription() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.networkStatus.collect {
                if (it == NetworkObserver.NetworkStatus.Available) {
                    binding.include.layoutNoConnection.visibility = View.GONE
                } else {
                    binding.include.layoutNoConnection.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun mileageSubscription() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.mileage.collect {
                binding.txtMileage.text = viewModel.mileage.value
            }
        }
    }

    private fun timeSubscription() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.time.collect {
                binding.txtHours.text = viewModel.time.value
            }
        }
    }

    private fun setupSubscriptions() {
        routeSubscription()
        networkSubscription()
        mileageSubscription()
        timeSubscription()
    }
}