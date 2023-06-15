package com.alefmoreira.citytraveltracker.views.fragments

import android.os.Bundle
import android.view.View
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
import com.alefmoreira.citytraveltracker.adapters.PlacePredictionAdapter
import com.alefmoreira.citytraveltracker.databinding.FragmentSearchRouteBinding
import com.alefmoreira.citytraveltracker.util.components.PredictionDividerItemDecoration
import com.alefmoreira.citytraveltracker.views.HomeViewModel
import com.alefmoreira.citytraveltracker.views.SearchRouteViewModel
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchRouteFragment : Fragment(R.layout.fragment_search_route) {

    private lateinit var binding: FragmentSearchRouteBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val searchRouteViewModel: SearchRouteViewModel by activityViewModels()
    private val token = AutocompleteSessionToken.newInstance()
    private val arguments: SearchRouteFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchRouteBinding.bind(view)

        val txtSearch = binding.txtSearch


        txtSearch.setEndIconOnClickListener {
            txtSearch.editText?.setText("")
        }
        txtSearch.setStartIconOnClickListener {
            homeViewModel.clearRoutes()
            findNavController().popBackStack()
        }
        txtSearch.isHintAnimationEnabled = false
        txtSearch.editText?.addTextChangedListener {
            val text = it.toString()

            if (text.length > 2) {
                debounce(text)
            }
            if (text.isEmpty()) {
                searchRouteViewModel.clearPredictions()
            }
        }

        val predictionRecyclerView = binding.predictionRecyclerview

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchRouteViewModel.predictions.collectLatest {
                    predictionRecyclerView.apply {
                        adapter = PlacePredictionAdapter(it, onItemClick = {
                            if (arguments.isDestination) {
                                homeViewModel.setDestination(
                                    name = it.getPrimaryText(null).toString(),
                                    placeId = it.placeId
                                )
                            } else {
                                homeViewModel.setOrigin(
                                    name = it.getPrimaryText(null).toString(),
                                    placeId = it.placeId
                                )
                            }
                            findNavController().popBackStack()
                        })
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
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                homeViewModel.clearRoutes()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun debounce(text: String) {
        searchRouteViewModel.findPredictions(text, token)
    }
}