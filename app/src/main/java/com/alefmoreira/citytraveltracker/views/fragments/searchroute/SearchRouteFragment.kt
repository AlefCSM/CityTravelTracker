package com.alefmoreira.citytraveltracker.views.fragments.searchroute

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.FragmentSearchRouteBinding
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.util.CitySelectionTypeEnum
import com.alefmoreira.citytraveltracker.util.components.PredictionDividerItemDecoration
import com.alefmoreira.citytraveltracker.util.components.adapters.PlacePredictionAdapter
import com.alefmoreira.citytraveltracker.views.fragments.route.RouteViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchRouteFragment : Fragment(R.layout.fragment_search_route) {

    private lateinit var binding: FragmentSearchRouteBinding
    private val routeViewModel: RouteViewModel by activityViewModels()
    private val searchRouteViewModel: SearchRouteViewModel by activityViewModels()
    private val token = AutocompleteSessionToken.newInstance()
    private val arguments: SearchRouteFragmentArgs by navArgs()

    private lateinit var txtSearch: TextInputLayout
    private lateinit var predictionRecyclerView: RecyclerView
    private lateinit var predictionRecyclerViewAdapter: PlacePredictionAdapter
    private lateinit var predictionNotFound: LinearLayoutCompat
    private lateinit var loadingDots: LinearLayoutCompat
    private lateinit var leftDot: ImageView
    private lateinit var middleDot: ImageView
    private lateinit var rightDot: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchRouteBinding.bind(view)

        bindViews(binding)
        setupClickListeners()

        txtSearch.isHintAnimationEnabled = false
        txtSearch.editText?.addTextChangedListener {
            searchRouteViewModel.validateText(it.toString(), token)
        }
        txtSearch.editText?.setText(arguments.selectedCity)

        predictionRecyclerViewAdapter = PlacePredictionAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchRouteViewModel.predictionStatus.collect { resource ->
                    handleState(resource)
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun bindViews(binding: FragmentSearchRouteBinding) {
        txtSearch = binding.txtSearch
        predictionRecyclerView = binding.predictionRecyclerview
        predictionNotFound = binding.predictionNotFound
        loadingDots = binding.loadingDots
        leftDot = binding.leftDot
        middleDot = binding.middleDot
        rightDot = binding.rightDot
    }

    private fun setupClickListeners() {
        txtSearch.setEndIconOnClickListener {
            txtSearch.editText?.setText("")
        }
        txtSearch.setStartIconOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun handleState(resource: Resource<List<AutocompletePrediction>>) {
        when (resource.status) {
            Status.SUCCESS -> {
                resource.data?.let { list ->
                    resource.message?.let { message -> renderList(list, message) }
                }
                predictionRecyclerView.visibility = View.VISIBLE
                predictionNotFound.visibility = View.GONE
                loadingDots.visibility = View.GONE
            }
            Status.ERROR -> {
                predictionRecyclerView.visibility = View.GONE
                predictionNotFound.visibility = View.VISIBLE
                loadingDots.visibility = View.GONE
            }
            Status.LOADING -> {
                startLoadingAnimation()
                predictionRecyclerView.visibility = View.GONE
                predictionNotFound.visibility = View.GONE
                loadingDots.visibility = View.VISIBLE
            }
            Status.INIT -> {
                predictionRecyclerView.visibility = View.GONE
                predictionNotFound.visibility = View.GONE
                loadingDots.visibility = View.GONE
            }
        }
    }

    private fun renderList(list: List<AutocompletePrediction>, text: String) {

        predictionRecyclerViewAdapter.apply {
            onItemClick = { prediction ->
                addCity(prediction)

                txtSearch.editText?.setText("")
                findNavController().popBackStack()
            }
            predictions = list
            typedText = text
        }

        predictionRecyclerView.apply {
            adapter = predictionRecyclerViewAdapter
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                PredictionDividerItemDecoration(
                    context,
                    R.drawable.prediction_line_divider
                )
            )
        }
    }

    private fun startLoadingAnimation() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.jump)
        val animation2 = AnimationUtils.loadAnimation(context, R.anim.jump2)
        val animation3 = AnimationUtils.loadAnimation(context, R.anim.jump3)

        leftDot.startAnimation(animation)
        middleDot.startAnimation(animation2)
        rightDot.startAnimation(animation3)
    }

    private fun addCity(prediction: AutocompletePrediction) {
        when (arguments.citySelectionEnum) {
            CitySelectionTypeEnum.ORIGIN -> {
                routeViewModel.setOrigin(
                    name = prediction.getPrimaryText(null).toString(),
                    placeId = prediction.placeId
                )
            }
            CitySelectionTypeEnum.DESTINATION -> {
                routeViewModel.setDestination(
                    name = prediction.getPrimaryText(null).toString(),
                    placeId = prediction.placeId
                )
            }
            CitySelectionTypeEnum.CONNECTION -> {
                routeViewModel.addConnection(
                    name = prediction.getPrimaryText(null).toString(),
                    placeId = prediction.placeId,
                    position = arguments.connectionEditionPosition
                )
            }
        }
    }
}