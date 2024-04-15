package com.alefmoreira.citytraveltracker.views.fragments.searchroute

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.FragmentSearchRouteBinding
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.util.CitySelectionTypeEnum
import com.alefmoreira.citytraveltracker.util.components.PredictionDividerItemDecoration
import com.alefmoreira.citytraveltracker.util.components.adapters.PlacePredictionAdapter
import com.alefmoreira.citytraveltracker.views.fragments.route.RouteViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchRouteFragment : Fragment(R.layout.fragment_search_route) {

    private lateinit var binding: FragmentSearchRouteBinding
    private val routeViewModel: RouteViewModel by activityViewModels()
    private val searchRouteViewModel: SearchRouteViewModel by activityViewModels()
    private val arguments: SearchRouteFragmentArgs by navArgs()

    private lateinit var predictionRecyclerViewAdapter: PlacePredictionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchRouteBinding.bind(view)

        setupClickListeners()

        binding.txtSearch.apply {
            isHintAnimationEnabled = false
            editText?.addTextChangedListener {
                searchRouteViewModel.validateText(it.toString())
            }
            editText?.setText(arguments.selectedCity)
        }


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

    private fun setupClickListeners() {
        binding.txtSearch.setEndIconOnClickListener {
            binding.txtSearch.editText?.setText("")
        }
        binding.txtSearch.setStartIconOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun handleState(resource: Resource<List<AutocompletePrediction>>) {
        when (resource.status) {
            Status.SUCCESS -> {
                resource.data?.let { list ->
                    resource.message?.let { message -> renderList(list, message) }
                }
                binding.predictionRecyclerview.visibility = View.VISIBLE
                binding.predictionNotFound.visibility = View.GONE
                binding.loadingDots.visibility = View.GONE
            }
            Status.ERROR -> {
                binding.predictionRecyclerview.visibility = View.GONE
                binding.predictionNotFound.visibility = View.VISIBLE
                binding.loadingDots.visibility = View.GONE
            }
            Status.LOADING -> {
                startLoadingAnimation()
                binding.predictionRecyclerview.visibility = View.GONE
                binding.predictionNotFound.visibility = View.GONE
                binding.loadingDots.visibility = View.VISIBLE
            }
            Status.INIT -> {
                binding.predictionRecyclerview.visibility = View.GONE
                binding.predictionNotFound.visibility = View.GONE
                binding.loadingDots.visibility = View.GONE
            }
        }
    }

    private fun renderList(list: List<AutocompletePrediction>, text: String) {

        predictionRecyclerViewAdapter.apply {
            onItemClick = { prediction ->
                addCity(prediction)

                binding.txtSearch.editText?.setText("")
                findNavController().popBackStack()
            }
            predictions = list
            typedText = text
        }

        binding.predictionRecyclerview.apply {
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

        binding.leftDot.startAnimation(animation)
        binding.middleDot.startAnimation(animation2)
        binding.rightDot.startAnimation(animation3)
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