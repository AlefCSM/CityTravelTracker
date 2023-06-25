package com.alefmoreira.citytraveltracker.views.fragments.route

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.BuildConfig.MAPS_API_KEY
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.adapters.AddConnectionAdapter
import com.alefmoreira.citytraveltracker.data.Connection
import com.alefmoreira.citytraveltracker.databinding.FragmentRouteBinding
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.util.CitySelectionTypeEnum
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch

class RouteFragment : Fragment(R.layout.fragment_route) {
    private val routeViewModel: RouteViewModel by activityViewModels()
    private lateinit var binding: FragmentRouteBinding
    private lateinit var originLayout: LinearLayoutCompat
    private lateinit var destinationLayout: LinearLayoutCompat
    private lateinit var connectionLayout: LinearLayoutCompat
    private lateinit var btnAddConnections: LinearLayoutCompat
    private lateinit var txtOrigin: TextView
    private lateinit var txtDestination: TextView
    private lateinit var connectionRecyclerView: RecyclerView
    private lateinit var btnSaveRoute: Button
    private lateinit var iconBack: ImageView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRouteBinding.bind(view)

        btnSaveRoute = binding.btnSaveRoute
        bindViews(binding)
        setupClickListeners()
        setupSubscriptions()

        val apiKey = MAPS_API_KEY
        context?.let { Places.initialize(it, apiKey) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (routeViewModel.isFirstRoute()) {
                    originLayout.visibility = View.VISIBLE
                }
                setup()
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                routeViewModel.clearRoutes()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    private fun bindViews(binding: FragmentRouteBinding) {
        originLayout = binding.originLayout
        destinationLayout = binding.destinationLayout
        connectionLayout = binding.connectionLayout
        btnAddConnections = binding.btnAddConnections
        txtOrigin = binding.txtOrigin
        txtDestination = binding.txtDestination
        connectionRecyclerView = binding.connectionList
        iconBack = binding.iconBack
    }

    private fun setup() {
        val origin = routeViewModel.currentOrigin
        if (origin.city.name.isNotEmpty()) {
            txtOrigin.apply {
                text = origin.city.name
                setTextColor(resources.getColor(R.color.dark_3, null))
            }
        }
        val destination = routeViewModel.currentDestination
        if (destination.city.name.isNotEmpty()) {
            txtDestination.apply {
                text = destination.city.name
                setTextColor(resources.getColor(R.color.dark_3, null))
            }
            btnAddConnections.apply {
                val imageView = getChildAt(0) as ImageView
                val textView = getChildAt(1) as TextView
                val secondaryColor = resources.getColor(R.color.secondary_3, null)

                imageView.setColorFilter(secondaryColor)
                textView.setTextColor(secondaryColor)
            }
        }
        if (routeViewModel.isButtonEnabled()) {
            btnSaveRoute.isEnabled = true
        }

        renderList(routeViewModel.currentDestination.connections)

    }

    private fun setupClickListeners() {
        originLayout.setOnClickListener {
            navigate(CitySelectionTypeEnum.ORIGIN, routeViewModel.currentOrigin.city.name)
        }
        destinationLayout.setOnClickListener {
            navigate(CitySelectionTypeEnum.DESTINATION, routeViewModel.currentDestination.city.name)
        }
        btnAddConnections.setOnClickListener {
            if (routeViewModel.isDestinationEmpty().not()) {
                navigate(CitySelectionTypeEnum.CONNECTION, "")
            }
        }
        btnSaveRoute.setOnClickListener {
            if (!routeViewModel.isLoading) {
                routeViewModel.saveRoute()
            }
        }
        iconBack.setOnClickListener {
            routeViewModel.clearRoutes()
            findNavController().popBackStack()
        }
    }

    private fun setupSubscriptions() {
        viewLifecycleOwner.lifecycleScope.launch {
            routeViewModel.routeStatus.collect {
                if (it.status == Status.SUCCESS) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun renderList(connectionList: List<Connection>) {
        if (connectionList.isNotEmpty()) {
            connectionLayout.visibility = View.VISIBLE
            connectionRecyclerView.apply {

                adapter = AddConnectionAdapter(connectionList)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    private fun navigate(routeType: CitySelectionTypeEnum, selectedCity: String) {
        findNavController().navigate(
            RouteFragmentDirections.actionRouteFragmentToSearchRouteFragment(
                routeType,
                selectedCity
            )
        )
    }
}