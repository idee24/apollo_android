package com.evosticlabs.apollo.screens.address

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.evosticlabs.apollo.MainActivity
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.BackgroundBlue
import com.evosticlabs.apollo.ui.theme.ButtonBorderColor
import com.evosticlabs.apollo.ui.theme.ButtonColor
import com.evosticlabs.apollo.ui.theme.ButtonTextColor
import com.evosticlabs.apollo.ui.theme.DormantBlue
import com.evosticlabs.apollo.ui.theme.TextBlue
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.*
import java.util.*


/**
 *@Created by Yerimah on 30/04/2023.
 */


val placeAdder = mutableStateOf(0)


fun initGeoCoder(context: MainActivity, latLng: LatLng) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
    context.addressDisplay.value = addresses?.get(0)?.getAddressLine(0) ?: ""
    context.displayLocation.value = latLng
}

@Composable
fun AddressScreen(mainActivity: MainActivity, onAddressSelected: (String) -> Unit) {

    val coordinates = rememberSaveable { mutableStateOf(mainActivity.displayLocation.value) } // Initial coordinates (San Francisco)

    LaunchedEffect(placeAdder.value) {
        if (placeAdder.value > 0) {
            onAddressSelected.invoke(mainActivity.addressDisplay.value)
            placeAdder.value = 0
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(coordinates.value, 14f)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            coordinates.value = LatLng(cameraPositionState.position.target.latitude, cameraPositionState.position.target.longitude)
            initGeoCoder(mainActivity, LatLng(cameraPositionState.position.target.latitude, cameraPositionState.position.target.longitude))
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
    ) {

        Column(Modifier.fillMaxSize()) {

            Box(modifier = Modifier
                .weight(0.75f)
                .fillMaxWidth()) {

                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)

                )

                Image(
                    painter = painterResource(id = R.drawable.pin),
                    contentDescription = "marker",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.25f))

        }

        Column(
            Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.weight(1f))



            Card(modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BackgroundBlue,
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp
                    )
                )
            ) {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = BackgroundBlue)
                    .padding(16.dp)) {

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(R.string.location_details),
                        fontWeight = FontWeight.Bold,
                        color = TextBlue,
                        fontSize = 11.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = ButtonBorderColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                            .clickable {
                                autoCompleteFragmentScreen(mainActivity)
                            },
                        verticalAlignment = CenterVertically,
                    ) {

                        Icon(
                            Icons.Default.Search,
                            tint = TextBlue,
                            contentDescription = stringResource(R.string.search_ic),
                            modifier = Modifier.size(24.dp),
                        )

                        Text(
                            text = mainActivity.addressDisplay.value.ifEmpty { mainActivity.getString(R.string.enter_location) },
                            fontWeight = FontWeight.Bold,
                            color = TextBlue,
                            fontSize = 14.sp
                        )

                    }

                    Spacer(modifier = Modifier.padding(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .height(50.dp)
                            .border(
                                width = 2.dp,
                                shape = RoundedCornerShape(8.dp),
                                color = ButtonBorderColor
                            )
                            .background(color = ButtonColor, shape = RoundedCornerShape(8.dp))
                            .clickable {
                                onAddressSelected.invoke(mainActivity.addressDisplay.value)
                            },
                    ){
                        Text(
                            text = stringResource(R.string.select_address),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = ButtonTextColor,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }



                    Spacer(modifier = Modifier.padding(16.dp))


                }


            }

        }
    }
}

fun autoCompleteFragmentScreen(activity: MainActivity) {

    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    if (!Places.isInitialized()) {
        Places.initialize(activity, activity.getString(R.string.google_api_key), Locale.getDefault())
    }

    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
        .build(activity)
    activity.resultLauncher.launch(intent)
}

