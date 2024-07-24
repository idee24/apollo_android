package com.evosticlabs.apollo.screens.address

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.evosticlabs.apollo.MainActivity
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.DormantBlue
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.*


/**
 *@Created by Yerimah on 30/04/2023.
 */

private val addressDisplay = mutableStateOf("")
private val displayLocation = mutableStateOf(LatLng(6.5095, 3.3711))

fun initGeoCoder(context: Context, latLng: LatLng) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
    addressDisplay.value = addresses?.get(0)?.getAddressLine(0) ?: ""
}

@Composable
fun AddressScreen(mainActivity: MainActivity) {

    initGeoCoder(mainActivity, displayLocation.value)

    if (ActivityCompat.checkSelfPermission(
            mainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            mainActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
//        mainActivity.locationPermissionRequest.launch(arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    mainActivity.fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
        displayLocation.value = LatLng(location?.latitude ?: displayLocation.value.latitude, location?.longitude ?: displayLocation.value.longitude)
        initGeoCoder(mainActivity, displayLocation.value)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(displayLocation.value, 14f)
    }

    LaunchedEffect(displayLocation.value) {
        initGeoCoder(mainActivity, displayLocation.value)
    }



    Box(modifier = Modifier.fillMaxSize()) {

        Column(Modifier.fillMaxSize()) {

            Box(modifier = Modifier
                .weight(0.75f)
                .fillMaxWidth()) {

                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                ) {

                }

                Image(
                    painter = painterResource(id = R.drawable.pin_ic),
                    contentDescription = "marker",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.25f))

        }

        Column(Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    if (ActivityCompat.checkSelfPermission(
                            mainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            mainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mainActivity.fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                            displayLocation.value = LatLng(location?.latitude ?: displayLocation.value.latitude, location?.longitude ?: displayLocation.value.longitude)
                            initGeoCoder(mainActivity, displayLocation.value)
                        }
                    }
                }) {

                    Box(modifier = Modifier
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.compass_ic),
                            contentDescription = "pass_change",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                }
            }

            Card(modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                )
            ) {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    Spacer(modifier = Modifier.padding(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = DormantBlue,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(10.dp)
                            .clickable {
//                               findNavController().navigate(R.id.action_addressFragment_to_placesFragment)
                            },
                        verticalAlignment = CenterVertically,
                    ) {

                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_ic),
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = mainActivity.getString(R.string.enter_location),
                            fontWeight = FontWeight.Bold,
                            color = DormantBlue,
                            fontSize = 14.sp
                        )

                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(R.string.location_details),
                        fontWeight = FontWeight.Bold,
                        color = DormantBlue,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.padding(6.dp))

                    Row(verticalAlignment = CenterVertically,
                        modifier = Modifier.fillMaxWidth()) {

                        Icon(
                            painter = painterResource(id = R.drawable.place_ico),
                            contentDescription = stringResource(R.string.search_ic),
                            modifier = Modifier.size(24.dp)
                        )


                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(
                            text = addressDisplay.value,
                            fontWeight = FontWeight.Bold,
                            color = DormantBlue,
                            fontSize = 13.sp,
                        )

                    }


                    Spacer(modifier = Modifier.padding(12.dp))

                    Button(
                        content = {
                            Text(text = stringResource(R.string.select_address), style = androidx.compose.material.MaterialTheme.typography.button)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {
//                            updateLocation(displayLocation.value, addressDisplay.value)
                        },
                        enabled = true,
                        shape = RoundedCornerShape(24.dp),
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                }


            }

        }
    }
}

