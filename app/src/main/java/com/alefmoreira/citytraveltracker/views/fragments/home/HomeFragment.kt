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
import com.alefmoreira.citytraveltracker.other.Constants.CALCULUS_ERROR
import com.alefmoreira.citytraveltracker.other.Constants.FEW_ELEMENTS_ERROR
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.util.components.AMAnimator
import com.alefmoreira.citytraveltracker.util.components.adapters.RouteAdapter
import com.alefmoreira.citytraveltracker.util.components.dialogs.AMAlertDialog
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

        val animation = AnimationUtils.loadAnimation(context, R.anim.presenting)
        binding.metricLayout.startAnimation(animation)

        viewModel.getRoutes()
        setupSubscriptions()
    }

    private fun routeSubscription() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.recyclerList.collect { resource ->
                if (resource.data.isNullOrEmpty()) {
                    binding.layoutNoRoutes.visibility = View.VISIBLE
                    binding.layoutRoutes.visibility = View.GONE
                } else {
                    binding.layoutNoRoutes.visibility = View.GONE
                    binding.layoutRoutes.visibility = View.VISIBLE

                    routeAdapter = RouteAdapter()
                    routeAdapter.apply {
                        routes = resource.data
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
                binding.txtMileage.text =
                    String.format(resources.getString(R.string.km, viewModel.mileage.value))
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

    private fun matrixSubscription() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.distanceMatrixStatus.collect {

                if (it.peekContent().status == Status.ERROR) {

                    when (it.peekContent().message) {
                        FEW_ELEMENTS_ERROR -> showAlertMessage(
                            title = resources.getString(R.string.matrix_error_title),
                            message = resources.getString(R.string.matrix_error_few_elements)
                        )

                        CALCULUS_ERROR -> showAlertMessage(
                            title = resources.getString(R.string.matrix_error_title),
                            message = resources.getString(R.string.matrix_error_calculus)
                        )

                        else -> showAlertMessage(
                            title = resources.getString(R.string.matrix_error_title),
                            message = " "
                        )
                    }
                }
            }
        }
    }

    private fun showAlertMessage(title: String, message: String) {
        val dialog = AMAlertDialog(requireContext())
        dialog.title = title
        dialog.message = message
        dialog.show()
    }

    private fun setupSubscriptions() {
        routeSubscription()
        networkSubscription()
        mileageSubscription()
        timeSubscription()
        matrixSubscription()
    }
}