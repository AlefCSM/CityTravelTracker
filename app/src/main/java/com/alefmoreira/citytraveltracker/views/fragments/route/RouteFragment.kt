package com.alefmoreira.citytraveltracker.views.fragments.route

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.alefmoreira.citytraveltracker.BuildConfig.MAPS_API_KEY
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.FragmentRouteBinding
import com.alefmoreira.citytraveltracker.util.CitySelectionType
import com.alefmoreira.citytraveltracker.views.fragments.home.HomeViewModel
import com.google.android.libraries.places.api.Places

class RouteFragment : Fragment(R.layout.fragment_route) {
    private lateinit var binding: FragmentRouteBinding
    private val viewModel: HomeViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRouteBinding.bind(view)

        val btnSearchRoute = binding.btnSearchRoute

        val apiKey = MAPS_API_KEY
        context?.let { Places.initialize(it, apiKey) }
        btnSearchRoute.setOnClickListener {
            val citySelectionType = CitySelectionType.ORIGIN
            findNavController().navigate(
                RouteFragmentDirections.actionRouteFragmentToSearchRouteFragment(
                    citySelectionType
                )
            )
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.clearRoutes()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }
}