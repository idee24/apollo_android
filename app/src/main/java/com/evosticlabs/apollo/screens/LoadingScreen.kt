package com.evosticlabs.apollo.screens

import android.view.LayoutInflater
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
import androidx.compose.ui.viewinterop.AndroidView
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.TextBlue

/**
 *@Created by Yerimah on 22/07/2024.
 */
@Composable
fun LoadingFrame(message: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(R.layout.loader, null, false)
            view

        }
    )

}