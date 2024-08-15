package com.evosticlabs.apollo

import android.app.Activity
import android.content.DialogInterface
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.evosticlabs.apollo.screens.Address
import com.evosticlabs.apollo.screens.Feature
import com.evosticlabs.apollo.screens.FeatureScreen
import com.evosticlabs.apollo.screens.Landing
import com.evosticlabs.apollo.screens.LandingScreen
import com.evosticlabs.apollo.screens.LoadingFrame
import com.evosticlabs.apollo.screens.Result
import com.evosticlabs.apollo.screens.ResultScreen
import com.evosticlabs.apollo.screens.Splash
import com.evosticlabs.apollo.screens.SplashScreen
import com.evosticlabs.apollo.screens.address.AddressScreen
import com.evosticlabs.apollo.screens.address.initGeoCoder
import com.evosticlabs.apollo.screens.address.placeAdder
import com.evosticlabs.apollo.screens.getCasualtyTypes
import com.evosticlabs.apollo.screens.getInternationalOptions
import com.evosticlabs.apollo.screens.getTargetTypes
import com.evosticlabs.apollo.screens.getWeaponsTypes
import com.evosticlabs.apollo.ui.theme.ApolloTheme
import com.evosticlabs.apollo.utils.hideKeyboard
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale


class MainActivity : ComponentActivity() {


    val addressDisplay = mutableStateOf("")
    val displayLocation = mutableStateOf(LatLng(51.5072, 0.1276))
    val startDate = mutableStateOf("")
    val endDate = mutableStateOf("")
    val internationalType = mutableStateOf("Default")
    val weaponsType = mutableStateOf("Default")
    val casualties = mutableStateOf("Default")
    val targetType = mutableStateOf("Default")
    val results = mutableStateOf(Response())

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
        if (!Python.isStarted()) Python.start(AndroidPlatform(this))
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
                                hideLoader()
                                navController.navigate(Feature)
                            }

                        }
                        composable<Feature> {
                            FeatureScreen(
                                context = this@MainActivity,
                                navToAddress = {
                                    navController.navigate(Address)
                                },
                                navToResult = {
                                    lifecycleScope.launch {
                                        initApollo {
                                            navController.navigate(Result)
                                        }
                                    }
                                }
                            )
                        }
                        composable<Address> {
                            AddressScreen(mainActivity = this@MainActivity) {
                                navController.navigate(Feature)
                            }
                        }
                        composable<Result> {
                            hideLoader()
                            ResultScreen(this@MainActivity) {
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

    fun getCountry(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<android.location.Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return addresses?.get(0)?.countryName ?: ""
    }

    private fun initApollo(navToResult: () -> Unit) {
        val py = Python.getInstance()
        val mod = py.getModule("apollo_engine")
        val path = copyRawResourceToInternalStorage()

        try {
            val res = mod.callAttr(
                "initApollo",
                path,
                startDate.value,
                endDate.value,
                getCasualtyCode(casualties.value),
                getTargetTypeCode(targetType.value),
                getWeaponsTypeCode(weaponsType.value),
                getInternationalType(internationalType.value),
                getCountry(displayLocation.value),
                displayLocation.value.latitude,
                displayLocation.value.longitude
            )
            val predictionResult = Gson().fromJson(res.toString(), Response::class.java)

            println("DDLS 00 Prediction ==> $res")

            if (predictionResult.code == 200) {
                results.value = predictionResult
                navToResult.invoke()
            }
            else {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.problem_text))
                    .show()
            }
        }
        catch (e: Exception) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.apollo_error))
                .setMessage(e.message)
                .show()
        }

    }

    private fun copyRawResourceToInternalStorage(): String {
        val inputStream = resources.openRawResource(R.raw.gtd_1)
        var outputStream: FileOutputStream? = null
        try {
            val outFile = File(filesDir, "gtd_1.csv")
            outputStream = FileOutputStream(outFile)
            val buffer = ByteArray(1024)
            var read: Int
            while ((inputStream.read(buffer).also { read = it }) != -1) {
                outputStream.write(buffer, 0, read)
            }
            inputStream.close()
            outputStream.flush()
            outputStream.close()

            // Get the file path
            return outFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(e.message)
                .show()
            return ""
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

fun getCasualtyCode(casualty: String): Int? {
    return when (casualty) {
        "High" -> 25
        "Medium" -> 12
        "Low" -> 2
        "None" -> 0
        else -> 0
    }
}

fun getWeaponsTypeCode(type: String): Int? {
    return when (type) {
        "Default" -> 0
        "Explosives" -> 6
        "Firearms" -> 5
        "Unknown" -> 13
        "Melee" -> 9
        "Sabotage Equipment" -> 11
        "Incendiary" -> 8
        "Vehicle (not to include vehicle-borne explosives, i.e., car or truck bombs)" -> 10
        "Other" -> 12
        "Chemical" -> 2
        else -> 0
    }
}

fun getTargetTypeCode(type: String): Int? {
    return when(type) {
        "Default" -> 0
        "Military" -> 4
        "Police" -> 3
        "Private Citizens & Property" -> 14
        "Journalists & Media" -> 10
        "Government (General)" -> 2
        "Unknown" -> 20
        "Utilities" -> 21
        "Business" -> 1
        "Terrorists/Non-State Militia" -> 17
        "Educational Institution" -> 8
        "Religious Figures/Institutions" -> 15
        else -> 0
    }
}

fun getInternationalType(type: String): Int? {
    return when (type) {
        "No" -> 0
        "Yes" -> 1
        "Unknown" -> 9
        else -> 0
    }
}


data class Response(
    var code: Int = 400,
    var message: String = "NaN",
    var refinedSummary: String = "",
    var results: LinkedHashMap<String, Prediction> = linkedMapOf()
)


data class Prediction(
    var featureImportance: String,
    var timeSeries: LinkedHashMap<String, Double>,
    var analysisSummary: String
)

