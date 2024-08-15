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
import com.evosticlabs.apollo.MainActivity
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.ActionBlue
import com.evosticlabs.apollo.ui.theme.BackgroundBlue
import com.evosticlabs.apollo.ui.theme.ButtonBorderColor
import com.evosticlabs.apollo.ui.theme.ButtonColor
import com.evosticlabs.apollo.ui.theme.TextBlue

/**
 *@Created by Yerimah on 22/07/2024.
 */
@Composable
fun LandingScreen(context: MainActivity, navToFeature: () -> Unit) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundBlue)
            .padding(top = 16.dp)
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Box {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "app Logo",
                modifier = Modifier
                    .size(width = 200.dp, height = 100.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.landing_photo),
                contentDescription = "app image",
                modifier = Modifier
                    .size(width = 700.dp, height = 350.dp)
                    .padding(horizontal = 16.dp)
            )

        }



        Text(
            text = stringResource(R.string.select_prediction_model),
            textAlign = TextAlign.Start,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlue,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 24.dp)
        )

        val selectedModel = rememberSaveable { mutableStateOf("Random Forest") }
        getTrainingModels().forEach { trainingModel ->
            FeatureItem(name = trainingModel, isSelected = true) {
//                selectedModel.value = trainingModel
                selectedModel.value = "Random Forest"
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.train_button),
            contentDescription = "train_button",
            modifier = Modifier
                .size(height = 80.dp, width = 700.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp)
                .clickable {
                    if (selectedModel.value.isNotEmpty()) {
                        context.showLoader()
                        Handler(Looper.getMainLooper()).postDelayed({
                            navToFeature.invoke()
                        }, 2000)
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

@Composable
fun FeatureItem(name: String,
                isSelected: Boolean,
                onSelect: (String) -> Unit) {

    Text(
        text = name,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextBlue,
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = ButtonBorderColor, shape = RoundedCornerShape(4.dp))
            .background(
                color = if (isSelected) ActionBlue else ButtonColor,
                shape = RoundedCornerShape(1.dp)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .clickable { onSelect.invoke(name) }
    )

}

fun getTrainingModels(): List<String> {
    return listOf("K-Neighbors Classifier", "Ada Boost Classifier", "Random Forest Classifier", "Gaussian Naive Bayes")
}

