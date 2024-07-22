package com.evosticlabs.apollo.screens

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.ActionBlue
import com.evosticlabs.apollo.ui.theme.BackgroundBlue
import com.evosticlabs.apollo.ui.theme.HighlightBlue
import com.evosticlabs.apollo.screens.Feature
import com.evosticlabs.apollo.ui.theme.LoaderBackgroundBlue

/**
 *@Created by Yerimah on 22/07/2024.
 */
@Composable
fun LandingScreen(context: Context, navController: NavController) {


    val isLoading = rememberSaveable { mutableStateOf(false) }

    if (isLoading.value) {
        LoadingFrame()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = BackgroundBlue)
                .padding(top = 16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "app Logo",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.landing_photo),
                contentDescription = "app image",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )


            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.select_prediction_model),
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(32.dp)
            )

            val selectedModel = rememberSaveable { mutableStateOf("") }
            getTrainingModels().forEach { trainingModel ->
                FeatureItem(name = trainingModel, isSelected = selectedModel.value == trainingModel) {
                    selectedModel.value = trainingModel
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.train_button),
                contentDescription = "train_button",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
                    .clickable {
                        if (selectedModel.value.isNotEmpty()) {
                            isLoading.value = true
                            Handler(Looper.getMainLooper()).postDelayed({
                                isLoading.value = false
                                navController.navigate(Feature)
                            }, 5000)
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    context.getString(R.string.select_training_model_to_continue),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
            )


            Spacer(modifier = Modifier.weight(1f))

        }



    }



}

@Composable
fun FeatureItem(name: String,
                isSelected: Boolean,
                onSelect: (String) -> Unit) {

    Text(
        text = name,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = HighlightBlue,
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = HighlightBlue, shape = RoundedCornerShape(8.dp))
            .background(
                color = if (isSelected) ActionBlue else BackgroundBlue,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            .clickable { onSelect.invoke(name) }
    )

}

fun getTrainingModels(): List<String> {
    return listOf("Logistic Regression", "Decision Tree", "Naive Bayes", "Random Forest", "KNN")
}

@Composable
fun LoadingFrame() {

    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading)
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = LoaderBackgroundBlue)
    ) {

        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "app Logo",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp)
        )


        LottieAnimation(
            composition = preloaderLottieComposition,
            progress = preloaderProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Text(
            text = stringResource(R.string.training_in_progress_please_wait),
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            color = HighlightBlue,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(32.dp)
        )

    }

}