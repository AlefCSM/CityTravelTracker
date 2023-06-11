package com.alefmoreira.citytraveltracker.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.databinding.FragmentSearchRouteBinding
import com.alefmoreira.citytraveltracker.views.HomeViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchRouteFragment : Fragment(R.layout.fragment_search_route) {

    private lateinit var binding: FragmentSearchRouteBinding
    private val viewModel: HomeViewModel by activityViewModels()

    @Inject
    lateinit var placesClient: PlacesClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchRouteBinding.bind(view)

        val txtHello = binding.txtHello

        val token = AutocompleteSessionToken.newInstance()

        context?.let {
            txtHello.setOnClickListener {
                val request =
                    FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery("paris")
                        .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                        for (prediction in response.autocompletePredictions) {
                            Log.i("****", "$prediction")
                            Log.i("****", prediction.placeId)
                            Log.i("****", prediction.getPrimaryText(null).toString())
                        }
                    }.addOnFailureListener { exception: Exception? ->
                        if (exception is ApiException) {
                            Log.e("****", "Place not found: " + exception.statusCode)
                            Log.e("****", "Place not found: " + exception.message)
                        }
                    }
            }
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