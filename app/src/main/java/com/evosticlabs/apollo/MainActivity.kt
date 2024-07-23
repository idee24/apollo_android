package com.evosticlabs.apollo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.evosticlabs.apollo.ui.theme.ApolloTheme
import kotlinx.serialization.Serializable
import androidx.navigation.compose.composable
import com.evosticlabs.apollo.screens.Feature
import com.evosticlabs.apollo.screens.FeatureScreen
import com.evosticlabs.apollo.screens.Landing
import com.evosticlabs.apollo.screens.Result
import com.evosticlabs.apollo.screens.LandingScreen
import com.evosticlabs.apollo.screens.LoadingFrame
import com.evosticlabs.apollo.screens.ResultScreen
import com.evosticlabs.apollo.screens.Splash
import com.evosticlabs.apollo.screens.SplashScreen
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient


class MainActivity : ComponentActivity() {

    lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Places.initialize(applicationContext, getString(R.string.google_api_key))
        placesClient = Places.createClient(this)
        enableEdgeToEdge()
        setContent {


            ApolloTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Splash
                ) {
                    composable<Splash> {
                        SplashScreen()
                        Handler(Looper.getMainLooper()).postDelayed({
                            navController.navigate(Landing)
                        }, 5000)
                    }
                    composable<Landing> {
                        LandingScreen(this@MainActivity) {
                            navController.navigate(Feature)
                        }

                    }
                    composable<Feature> {
                        FeatureScreen(this@MainActivity) {
                            navController.navigate(Result)
                        }
                    }
                    composable<Result> {
                        ResultScreen(navController)
                    }
                }
            }
        }
    }
}



