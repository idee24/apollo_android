package com.evosticlabs.apollo.screens

import android.app.DatePickerDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.evosticlabs.apollo.MainActivity
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.BackgroundBlue
import com.evosticlabs.apollo.ui.theme.ButtonBorderColor
import com.evosticlabs.apollo.ui.theme.ButtonColor
import com.evosticlabs.apollo.ui.theme.ButtonTextColor
import com.evosticlabs.apollo.ui.theme.ErrorRed
import com.evosticlabs.apollo.ui.theme.FieldColor
import com.evosticlabs.apollo.ui.theme.LoaderBackgroundBlue
import com.evosticlabs.apollo.ui.theme.TextBlue
import com.evosticlabs.apollo.ui.theme.TextColorDisabled
import com.evosticlabs.apollo.ui.theme.appOutlineTextFieldColors
import com.evosticlabs.apollo.utils.CustomSpinner
import com.evosticlabs.apollo.utils.HintText
import com.evosticlabs.apollo.utils.getDateObject
import java.util.Calendar
import java.util.Date

/**
 *@Created by Yerimah on 22/07/2024.
 */



@Composable
fun FeatureScreen(context: MainActivity,
                  navToResult: () -> Unit,
                  navToAddress: () -> Unit) {

    val calendar = Calendar.getInstance()
    val startDate = rememberSaveable { mutableStateOf( context.startDate.value) }
    val endDate = rememberSaveable { mutableStateOf( context.endDate.value) }

    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

    val startPicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            startDate.value = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
            context.startDate.value = startDate.value
        }, year, month, dayOfMonth
    )

    val endPicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            endDate.value = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
            context.endDate.value = endDate.value
        }, year, month, dayOfMonth
    )

    val location =  rememberSaveable { mutableStateOf(context.addressDisplay.value) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundBlue)
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 24.dp,
                bottom = 24.dp
            )
    ) {

        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth())
        {

            Image(
                painter = painterResource(id = R.drawable.feature_background),
                contentDescription = "back Logo",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            )

            LazyColumn(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        Spacer(modifier = Modifier.padding(8.dp))

                        Text(
                            text = stringResource(R.string.feature_selection),
                            fontSize = 14.sp,
                            color = TextBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 40.dp)
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(
                            text = stringResource(R.string.prediction_timeframe),
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp,
                            color = TextBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp)
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (startDate.value.isEmpty()) TextColorDisabled else ButtonBorderColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(color = FieldColor, shape = RoundedCornerShape(8.dp))
                                .clickable {
                                    startPicker.show()
                                }
                        ) {

                            Text(
                                text = startDate.value.ifEmpty { stringResource(R.string.start_date) },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (startDate.value.isEmpty()) TextColorDisabled else ButtonTextColor,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(10.dp)
                            )
                        }


                        Spacer(modifier = Modifier.padding(8.dp))

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (endDate.value.isEmpty()) TextColorDisabled else ButtonBorderColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(color = FieldColor, shape = RoundedCornerShape(8.dp))
                                .clickable {
                                    endPicker.show()
                                }
                        ) {

                            Text(
                                text = endDate.value.ifEmpty { stringResource(R.string.end_date) },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (endDate.value.isEmpty()) TextColorDisabled else ButtonTextColor,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(10.dp)
                            )
                        }


                        Spacer(modifier = Modifier.padding(8.dp))


                        Text(
                            text = stringResource(R.string.location),
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlue,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp)
                        )


                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (location.value.isEmpty()) TextColorDisabled else ButtonBorderColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(color = FieldColor, shape = RoundedCornerShape(8.dp))
                                .clickable {
                                    navToAddress.invoke()
                                }
                        ) {

                            Text(
                                text = location.value.ifEmpty { stringResource(R.string.enter_location) },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (location.value.isEmpty()) TextColorDisabled else ButtonTextColor,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.padding(8.dp))



                        Text(
                            text = stringResource(R.string.casualties),
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlue,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp)
                        )

                        val casualty = rememberSaveable{ mutableStateOf(context.casualties.value) }
                        val casualtyTypes = getCasualtyTypes()

                        CustomSpinner(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            items = casualtyTypes,
                            initialSelection = 0
                        ) {
                            casualty.value = casualtyTypes[it]
                            context.casualties.value = casualty.value
                        }

                        Text(
                            text = stringResource(R.string.target_type),
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlue,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp)
                        )

                        val target = rememberSaveable{ mutableStateOf(context.targetType.value) }
                        val targetTypes = getTargetTypes()

                        CustomSpinner(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            items = targetTypes,
                            initialSelection = 0
                        ) {
                            target.value = targetTypes[it]
                            context.targetType.value = target.value
                        }

                        Text(
                            text = stringResource(R.string.weapon_type),
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlue,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp)
                        )

                        val weaponsType = rememberSaveable{ mutableStateOf(context.weaponsType.value) }
                        val weaponsTypes = getWeaponsTypes()

                        CustomSpinner(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            items = weaponsTypes,
                            initialSelection = 0
                        ) {
                            weaponsType.value = weaponsTypes[it]
                            weaponsType.value = weaponsType.value
                        }

                        Text(
                            text = stringResource(R.string.attack_type),
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlue,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp)
                        )

                        val attackType = rememberSaveable{ mutableStateOf(context.internationalType.value) }
                        val attackTypes = getInternationalOptions()

                        CustomSpinner(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            items = attackTypes,
                            initialSelection = 0
                        ) {
                            attackType.value = attackTypes[it]
                            context.internationalType.value = attackType.value
                        }

                        Spacer(modifier = Modifier.padding(40.dp))

                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
                .border(width = 2.dp, shape = RoundedCornerShape(8.dp), color = ButtonBorderColor)
                .background(color = ButtonColor, shape = RoundedCornerShape(8.dp))
                .clickable {
                    if (isValidFeatures(context)) {
                        context.showLoader()
                        Handler(Looper.getMainLooper()).postDelayed({
                            navToResult.invoke()
                        }, 500)
                    }
                },
        ){
            Text(
                text = stringResource(R.string.submit),
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

fun isValidFeatures(mainActivity: MainActivity): Boolean {
    if (isValidDate(mainActivity)) {
        if (mainActivity.addressDisplay.value.isNotEmpty()) {
            return true
        }
        else {
            AlertDialog.Builder(mainActivity)
                .setTitle(mainActivity.getString(R.string.invalid_location))
                .setMessage(mainActivity.getString(R.string.provide_location))
                .show()
            return false
        }
    }
    else return false
}

fun isValidDate(mainActivity: MainActivity): Boolean {
    var isValidDate = false
    val startDate = getDateObject(mainActivity.startDate.value)
    val endDate = getDateObject(mainActivity.endDate.value)
    val isFutureDate = (startDate.time > Date((Date().time) - (94 * 60 * 60 * 1000)).time)
    isValidDate = ((endDate.time > startDate.time) &&
                    isIntervalGreaterThanOneMonth(startDate, endDate))
    if (isValidDate) {
        if (isFutureDate) {
            return true
        }
        else {
            AlertDialog.Builder(mainActivity)
                .setTitle(mainActivity.getString(R.string.invalid_prediction_timeframe))
                .setMessage(mainActivity.getString(R.string.future_date))
                .show()
            return false
        }
    }
    else {
        AlertDialog.Builder(mainActivity)
            .setTitle(mainActivity.getString(R.string.invalid_prediction_timeframe))
            .setMessage(mainActivity.getString(R.string.please_select_a_future_period_longer_than_a_month))
            .show()
        return false
    }
}

fun isIntervalGreaterThanOneMonth(startDate: Date, endDate: Date): Boolean {
    val startCalendar = Calendar.getInstance().apply { time = startDate }
    val endCalendar = Calendar.getInstance().apply { time = endDate }

    val startYear = startCalendar.get(Calendar.YEAR)
    val startMonth = startCalendar.get(Calendar.MONTH)

    val endYear = endCalendar.get(Calendar.YEAR)
    val endMonth = endCalendar.get(Calendar.MONTH)

    // If the difference in years is more than 0, the interval is definitely more than a month
    if (endYear > startYear) {
        return true
    }
    // If the years are the same, check the months
    if (endMonth > startMonth) {
        return true
    }
    return false
}

fun getCasualtyTypes():List<String> {
    return listOf("None", "High", "Medium", "Low")
}

fun getInternationalOptions():List<String> {
    return listOf("Unknown", "Yes", "No")
}

fun getTargetTypes():List<String> {
    return listOf(
        "Unknown",
        "Military",
        "Police",
        "Private Citizens & Property",
        "Journalists & Media",
        "Government (General)",
        "Food or Water Supply",
        "Business",
        "Terrorists/Non-State Militia",
        "Airports & Aircraft"
    )
}



fun getWeaponsTypes():List<String> {
    return listOf(
        "Unknown",
        "Explosives",
        "Firearms",
        "Melee",
        "Sabotage Equipment",
        "Incendiary",
        "Vehicle (not to include vehicle-borne explosives, i.e., car or truck bombs)",
        "Other",
        "Chemical"
    )
}

