package com.example.citytraveltracker.views

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.citytraveltracker.BuildConfig.MAPS_API_KEY
import com.example.citytraveltracker.R
import com.example.citytraveltracker.databinding.ActivityMainBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val btn_start = binding.btnStart


        val apiKey = MAPS_API_KEY
        Places.initialize(applicationContext, apiKey)

        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val place = Autocomplete.getPlaceFromIntent(data)
                    println("***** PLACE ID ${place.id} - PLACE NAME ${place.name}")
                }
            }
        }

        btn_start.setOnClickListener {
//            val fragmentManager = supportFragmentManager.beginTransaction()
//                .add(R.id.container, SearchAddressFragment()).commit()



//            private val AUTOCOMPLETE_REQUEST_CODE = 1

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.

//            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)



            resultLauncher.launch(intent)


        }



// Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).


//        val request =
//            FindAutocompletePredictionsRequest.builder()
////                 Call either setLocationBias() OR setLocationRestriction().
////                .setTypesFilter(listOf(TypeFilter.ADDRESS.toString(),TypeFilter.REGIONS.toString(),TypeFilter.CITIES.toString()))
//                .setSessionToken(token)
//                .setQuery("auburn")
//                .build()
//        placesClient.findAutocompletePredictions(request)
//            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
//                for (prediction in response.autocompletePredictions) {
//                    Log.i("****", prediction.placeId)
//                    Log.i("****", prediction.getPrimaryText(null).toString())
//                }
//            }.addOnFailureListener { exception: Exception? ->
//                if (exception is ApiException) {
//                    Log.e("****", "Place not found: " + exception.statusCode)
//                    Log.e("****", "Place not found: " + exception.message)
//                }
//            }



    }



    private fun showCard() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.fragment_search_address)
        dialog.show()
    }
}