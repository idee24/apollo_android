package com.evosticlabs.apollo.screens.address

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evosticlabs.apollo.MainActivity
import com.evosticlabs.apollo.ui.theme.appOutlineTextFieldColors
import com.evosticlabs.apollo.utils.HintText
import com.evosticlabs.apollo.utils.autofill
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.ActionBlue
import com.evosticlabs.apollo.ui.theme.DormantBlue

/**
 *@Created by Yerimah on 13/05/2023.
 */


private val addressError = mutableStateOf("")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlaceAdapterScreen(mainActivity: MainActivity) {

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val showKeyboard = remember { mutableStateOf(true) }
    val address = rememberSaveable { mutableStateOf( "") }
    val addressErrorText = rememberSaveable { addressError }
    val suggestions = rememberSaveable { mutableStateOf( listOf<AutocompletePrediction>()) }

    val token = AutocompleteSessionToken.newInstance()
    val request = FindAutocompletePredictionsRequest.builder()
        .setOrigin(LatLng(6.5095, 3.3711))
        .setCountries("NG")
        .setTypeFilter(TypeFilter.ADDRESS)
        .setSessionToken(token)
        .setQuery(address.value)
        .build()

    mainActivity.placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
            suggestions.value = response.autocompletePredictions
        }.addOnFailureListener { exception: Exception? ->
            if (exception is ApiException) {
                Log.e("Places", "Place not found: ${exception.statusCode}")
            }
        }



    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp))
    {

        Spacer(modifier = Modifier.padding(24.dp))

        androidx.compose.material3.OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { foc ->
                    if (showKeyboard.value != foc.isFocused) {
                        showKeyboard.value = foc.isFocused
                        if (!foc.isFocused) {
                            keyboard?.hide()
                        }
                    }
                }
                .autofill(
                    autofillTypes = listOf(
                        AutofillType.AddressStreet
                    ),
                    onFill = { address.value = it }
                ),
            value = address.value,
            onValueChange = { newValue ->
                if (newValue.length <= 255) {
                    address.value = newValue
                }
                addressErrorText.value = ""
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                },
            ),
            colors = appOutlineTextFieldColors(addressErrorText.value.isBlank()),
            shape = RoundedCornerShape(30.dp),
            textStyle = androidx.compose.material.MaterialTheme.typography.body1,
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_ic),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                HintText(
                    stringResource(R.string.enter_location),
                    addressErrorText.value.isNotBlank()
                )
            }, isError = addressErrorText.value.isNotBlank(), supportingText = {
                if (addressErrorText.value.isNotBlank()) {
                    Text(
                        text = addressErrorText.value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )


        Spacer(modifier = Modifier.padding(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable {
//                findNavController().navigateUp()
            }) {

            Image(
                painter = painterResource(id = R.drawable.compass_ic),
                contentDescription = "pass_change",
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = stringResource(R.string.set_location_on_map),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = ActionBlue,
                modifier = Modifier.padding(start = 16.dp)
            )

        }

        Spacer(modifier = Modifier.padding(4.dp))

        Divider(modifier = Modifier.fillMaxWidth())


        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                suggestions.value.forEach {
                    AddressItem(name = it.getPrimaryText(null).toString()) {
//                        updateLocation(
//                            LatLng(6.5095, 3.3711),
//                            it.getPrimaryText(null).toString()
//                        )
                    }
                }
            }
        }



    }

}

@Composable
fun AddressItem(name: String, onClick: () -> Unit) {

    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() }) {

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {

            Icon(
                painter = painterResource(id = R.drawable.place_ico),
                contentDescription = stringResource(R.string.search_ic),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                color = DormantBlue,
                maxLines = 2,
                fontSize = 13.sp,
            )

        }

        Divider(modifier = Modifier.fillMaxWidth())

    }

}