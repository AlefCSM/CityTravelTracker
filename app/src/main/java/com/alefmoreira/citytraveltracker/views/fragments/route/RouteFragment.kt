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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.BuildConfig.MAPS_API_KEY
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.data.Connection
import com.alefmoreira.citytraveltracker.databinding.FragmentRouteBinding
import com.alefmoreira.citytraveltracker.other.Constants.ADD_CONNECTION_ICON_POSITION
import com.alefmoreira.citytraveltracker.other.Constants.ADD_CONNECTION_TEXT_POSITION
import com.alefmoreira.citytraveltracker.other.Constants.DEFAULT_CONNECTION_POSITION
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.util.CitySelectionTypeEnum
import com.alefmoreira.citytraveltracker.util.DialogType
import com.alefmoreira.citytraveltracker.util.components.AMAnimator
import com.alefmoreira.citytraveltracker.util.components.adapters.AddConnectionAdapter
import com.alefmoreira.citytraveltracker.util.components.dialogs.AMConfirmationDialog
import com.alefmoreira.citytraveltracker.util.components.dialogs.AMLoadingDialog
import com.alefmoreira.citytraveltracker.views.fragments.home.HomeViewModel
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch

class RouteFragment : Fragment(R.layout.fragment_route) {
    private val routeViewModel: RouteViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentRouteBinding
    private lateinit var originLayout: LinearLayoutCompat
    private lateinit var destinationLayout: LinearLayoutCompat
    private lateinit var connectionLayout: LinearLayoutCompat
    private lateinit var btnAddConnections: LinearLayoutCompat
    private lateinit var txtOrigin: TextView
    private lateinit var txtDestination: TextView
    private lateinit var connectionRecyclerView: RecyclerView
    private lateinit var btnSaveRoute: Button
    private lateinit var btnDeleteRoute: Button
    private lateinit var iconBack: ImageView
    private lateinit var connectionRecyclerViewAdapter: AddConnectionAdapter
    private lateinit var dialog: AMLoadingDialog

    private val arguments: RouteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        routeViewModel.isFirstRoute.value = homeViewModel.isFirstRoute()

        if (arguments.routeId > 0) {
            routeViewModel.getRoute(arguments.routeId)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRouteBinding.bind(view)

        bindViews(binding)
        setupClickListeners()

        val apiKey = MAPS_API_KEY
        context?.let { Places.initialize(it, apiKey) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (routeViewModel.isFirstRoute.value) {
                    originLayout.visibility = View.VISIBLE
                }
                setup()
                setupSubscriptions()

                if (routeViewModel.currentDestination.connections.isNotEmpty()) {
                    connectionLayout.visibility = View.VISIBLE
                    renderList(routeViewModel.currentDestination.connections)
                }

                routeViewModel.destinationStatus.collect {
                    it.data?.let { route ->
                        if (route.connections.isEmpty()) {
                            connectionLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showLeaveMessage()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    private fun bindViews(binding: FragmentRouteBinding) {
        btnAddConnections = binding.btnAddConnections
        btnDeleteRoute = binding.btnDeleteRoute
        btnSaveRoute = binding.btnSaveRoute
        connectionLayout = binding.connectionLayout
        connectionRecyclerView = binding.connectionList
        destinationLayout = binding.destinationLayout
        iconBack = binding.iconBack
        originLayout = binding.originLayout
        txtOrigin = binding.txtOrigin
        txtDestination = binding.txtDestination
    }

    private fun setup() {
        val origin = routeViewModel.currentOrigin
        dialog = AMLoadingDialog(requireContext())
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
                val imageView = getChildAt(ADD_CONNECTION_ICON_POSITION) as ImageView
                val textView = getChildAt(ADD_CONNECTION_TEXT_POSITION) as TextView
                val secondaryColor = resources.getColor(R.color.secondary_3, null)

                imageView.setColorFilter(secondaryColor)
                textView.setTextColor(secondaryColor)
            }
        }
        if (routeViewModel.isButtonEnabled()) {
            btnSaveRoute.isEnabled = true
        }
        if (arguments.routeId > 0) {
            btnDeleteRoute.visibility = View.VISIBLE
        }
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
        btnDeleteRoute.setOnClickListener {
            if (!routeViewModel.isLoading) {
                showDeleteMessage()
            }
        }
        iconBack.setOnClickListener {
            showLeaveMessage()
        }
    }

    private fun setupSubscriptions() =
        viewLifecycleOwner.lifecycleScope.launch {
            routeViewModel.routeStatus.collect {
                if (it.status == Status.LOADING) {
                    dialog.show()
                } else {
                    dialog.hide()
                }

                if (it.status == Status.SUCCESS) {
                    returnToHome()
                }
            }
        }

    private fun renderList(connectionList: List<Connection>) {
        connectionRecyclerViewAdapter = AddConnectionAdapter()
        connectionRecyclerView.apply {
            adapter = connectionRecyclerViewAdapter
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val animator = AMAnimator(context)
            itemAnimator = animator
        }
//        val test = connectionRecyclerView.itemAnimator
//        (test as SimpleItemAnimator).supportsChangeAnimations = false

        connectionRecyclerViewAdapter.connections = connectionList
        connectionRecyclerViewAdapter.onDeleteClick = {
            routeViewModel.removeConnection(it)
        }
        connectionRecyclerViewAdapter.onItemClick = {
            val position = routeViewModel.currentDestination.connections.indexOf(it)
            navigate(
                CitySelectionTypeEnum.CONNECTION,
                "",
                connectionEditionPosition = position
            )
        }
    }

    private fun navigate(
        routeType: CitySelectionTypeEnum,
        selectedCity: String,
        connectionEditionPosition: Int = DEFAULT_CONNECTION_POSITION
    ) {
        findNavController().navigate(
            RouteFragmentDirections.actionRouteFragmentToSearchRouteFragment(
                routeType,
                selectedCity,
                connectionEditionPosition
            )
        )
    }

    private fun showLeaveMessage() {
        val dialog = AMConfirmationDialog(requireContext(), DialogType.LEAVE)
        dialog.apply {
            onConfirm = {
                returnToHome()
            }
            this.show()
        }
    }

    private fun showDeleteMessage() {
        val route = routeViewModel.currentDestination
        val dialog = AMConfirmationDialog(requireContext(), DialogType.DELETE)
        dialog.apply {
            city = route.city.name
            onConfirm = {
                routeViewModel.deleteRoute(route)
                this.dismiss()
            }
            this.show()
        }
    }

    private fun returnToHome() {
        routeViewModel.clearRoutes()
        findNavController().popBackStack()
    }
}