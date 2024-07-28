package com.evosticlabs.apollo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.evosticlabs.apollo.ui.theme.ApolloTheme
import kotlinx.serialization.Serializable
import androidx.navigation.compose.composable
import com.evosticlabs.apollo.screens.Address
import com.evosticlabs.apollo.screens.Feature
import com.evosticlabs.apollo.screens.FeatureScreen
import com.evosticlabs.apollo.screens.Landing
import com.evosticlabs.apollo.screens.Result
import com.evosticlabs.apollo.screens.LandingScreen
import com.evosticlabs.apollo.screens.LoadingFrame
import com.evosticlabs.apollo.screens.PlaceAdapter
import com.evosticlabs.apollo.screens.ResultScreen
import com.evosticlabs.apollo.screens.Splash
import com.evosticlabs.apollo.screens.SplashScreen
import com.evosticlabs.apollo.screens.address.AddressScreen
import com.evosticlabs.apollo.screens.address.placeAdder
import com.evosticlabs.apollo.utils.hideKeyboard
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete


class MainActivity : ComponentActivity() {

    val addressDisplay = mutableStateOf("")
    val displayLocation = mutableStateOf(LatLng(51.5072, 0.1276))
    val startDate = mutableStateOf("")
    val EndDate = mutableStateOf("")
    val attackType = mutableStateOf("Default")
    val weaponsType = mutableStateOf("Default")
    val casualties = mutableStateOf("Default")
    val targetType = mutableStateOf("Default")



    lateinit var placesClient: PlacesClient
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val isLoading = mutableStateOf(false)

    val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val place = Autocomplete.getPlaceFromIntent(intent)
                addressDisplay.value = place.address ?: ""
                displayLocation.value = LatLng(place.latLng.latitude, place.latLng.longitude)
                placeAdder.value = 2
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, result.data.toString(), Toast.LENGTH_SHORT).show()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(applicationContext, getString(R.string.google_api_key))
        placesClient = Places.createClient(this)
        enableEdgeToEdge()
        setContent {

            var showLoader = rememberSaveable { isLoading }

            ApolloTheme {
                val navController = rememberNavController()

                Box(Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Splash
                    ) {
                        composable<Splash> {
                            SplashScreen()
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate(Landing)
                            }, 3000)
                        }
                        composable<Landing> {
                            LandingScreen(this@MainActivity) {
                                navController.navigate(Feature)
                            }

                        }
                        composable<Feature> {
                            hideLoader()
                            FeatureScreen(
                                context = this@MainActivity,
                                navToAddress = {
                                    navController.navigate(Address)
                                },
                                navToResult = {
                                    navController.navigate(Result)
                                }
                            )
                        }
                        composable<Address> {
                            AddressScreen(mainActivity = this@MainActivity) {
                                navController.navigate(Feature)
                            }
                        }
                        composable<Result> {
                            ResultScreen{
                                navController.navigate(Landing)
                            }
                        }

                    }

                    if (showLoader.value) {
                        LoadingFrame(message = "")
                    }
                }

            }
        }
    }

    fun showLoader() {
        this.actionBar?.customView?.hideKeyboard()
        isLoading.value = true
    }

    fun hideLoader() {
        isLoading.value = false
    }
}



