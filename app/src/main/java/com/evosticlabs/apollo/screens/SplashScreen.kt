package com.evosticlabs.apollo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.BackgroundBlue
import com.evosticlabs.apollo.ui.theme.TextBlue

/**
 *@Created by Yerimah on 22/07/2024.
 */
@Composable
fun SplashScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundBlue)
    ) {

        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "Splash Logo",
            modifier = Modifier
                .size(height = 300.dp, width = 700.dp)
                .align(Alignment.Center)
        )

        Text(
            text = "Copyright © 2016 MIT. All rights reserved.",
            textAlign = TextAlign.Center,
            color = TextBlue,
            fontSize = 10.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        )

    }

}