package com.evosticlabs.apollo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.evosticlabs.apollo.screens.ResultScreen
import com.evosticlabs.apollo.screens.Splash
import com.evosticlabs.apollo.screens.SplashScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApolloTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Splash
                ) {
                    composable<Splash> {
                        SplashScreen(navController)
                    }
                    composable<Landing> {
                        LandingScreen(navController)
                    }
                    composable<Feature> {
                        FeatureScreen(navController)
                    }
                    composable<Result> {
                        ResultScreen(navController)
                    }
                }
            }
        }
    }
}



