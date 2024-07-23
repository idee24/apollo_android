package com.evosticlabs.apollo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.LoaderBackgroundBlue
import com.evosticlabs.apollo.ui.theme.TextBlue

/**
 *@Created by Yerimah on 22/07/2024.
 */
@Composable
fun LoadingFrame(message: String) {

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
            .background(color = Color.White)
    ) {

        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "app Logo",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp)
        )


//        LottieAnimation(
//            composition = preloaderLottieComposition,
//            progress = preloaderProgress,
//            modifier = Modifier
//                .height(100.dp)
//        )

        Text(
            text = message,
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            color = TextBlue,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(32.dp)
        )

    }

}