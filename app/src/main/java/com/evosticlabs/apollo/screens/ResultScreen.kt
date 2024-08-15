package com.evosticlabs.apollo.screens

import android.icu.text.BreakIterator
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.evosticlabs.apollo.MainActivity
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.BackgroundBlue
import com.evosticlabs.apollo.ui.theme.ButtonBorderColor
import com.evosticlabs.apollo.ui.theme.ButtonColor
import com.evosticlabs.apollo.ui.theme.ButtonTextColor
import com.evosticlabs.apollo.ui.theme.TextBlue
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.delay
import java.text.StringCharacterIterator

/**
 *@Created by Yerimah on 22/07/2024.
 */
@Composable
fun ResultScreen(mainActivity: MainActivity, navToLanding: () -> Unit) {

    val predictions = mainActivity.results.value.results

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
        Image(
            painter = painterResource(id = R.drawable.prediction_header),
            contentDescription = "back Logo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(height = 40.dp, width = 400.dp)
        )


        Box(modifier = Modifier
            .weight(1.4f)
            .padding(16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = ButtonBorderColor,
                shape = AbsoluteCutCornerShape(topLeft = 16.dp, bottomRight = 16.dp)
            )
            .background(
                color = Color.White,
                shape = AbsoluteCutCornerShape(topLeft = 16.dp, bottomRight = 16.dp)
            )
        ) {

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = ButtonBorderColor,
                        shape = AbsoluteCutCornerShape(topLeft = 16.dp, bottomRight = 16.dp)
                    )
                    .background(
                        color = Color.White,
                        shape = AbsoluteCutCornerShape(topLeft = 16.dp, bottomRight = 16.dp)
                    ),
                factory = { context ->
                    val view = LayoutInflater.from(context).inflate(R.layout.chart, null, false)
                    val chart = view.findViewById<LineChart>(R.id.lineChart)

                    chart.setDrawGridBackground(true)
                    chart.description.isEnabled = false
                    chart.setDrawBorders(true)

                    chart.axisLeft.isEnabled = false
                    chart.axisRight.setDrawAxisLine(true)
                    chart.axisRight.setDrawGridLines(true)
                    chart.xAxis.setDrawAxisLine(true)
                    chart.xAxis.setDrawGridLines(true)

                    // enable touch gestures
                    chart.setTouchEnabled(true)

                    // enable scaling and dragging
                    chart.isDragEnabled = true
                    chart.setScaleEnabled(true)

                    // if disabled, scaling can be done on x- and y-axis separately
                    chart.setPinchZoom(false)

                    val l = chart.legend
                    l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    l.orientation = Legend.LegendOrientation.HORIZONTAL
                    l.setDrawInside(false)

                    chart.getAxisLeft().setTextColor(context.getColor(R.color.graph_text_color)) // left y-axis
                    chart.getXAxis().setTextColor(context.getColor(R.color.graph_text_color))
                    chart.getLegend().setTextColor(context.getColor(R.color.graph_text_color))
                    chart.getDescription().setTextColor(context.getColor(R.color.graph_text_color))

                    chart.setBorderColor(context.getColor(R.color.graph_text_color))
                    chart.setGridBackgroundColor(context.getColor(R.color.black))
                    chart.setBackgroundColor(context.getColor(R.color.black))
                    chart.setNoDataTextColor(context.getColor(R.color.graph_text_color))


                    chart.resetTracking()

                    val dataSets = ArrayList<ILineDataSet>()

                    predictions.forEach { (model, prediction) ->
                        val values = ArrayList<Entry>()
                        prediction.timeSeries.values.indices.forEachIndexed { index, prob ->
                            values.add(Entry(index.toFloat(), prob.toFloat()))
                        }

                        val d = LineDataSet(values, model)
                        d.lineWidth = 2.5f
                        d.circleRadius = 4f

                        val color: Int = colors.get(2 % colors.size)
                        d.color = color
                        d.setCircleColor(color)
                        dataSets.add(d)
                    }

                    // make the first DataSet dashed
                    (dataSets[0] as LineDataSet).enableDashedLine(10f, 10f, 0f)
                    (dataSets[0] as LineDataSet).setColors(*ColorTemplate.VORDIPLOM_COLORS)
                    (dataSets[0] as LineDataSet).setCircleColors(*ColorTemplate.VORDIPLOM_COLORS)

                    val data = LineData(dataSets)
                    chart.setData(data)
                    chart.animateXY(3000, 3000)
                    chart.invalidate()

                    view

                }
            )

        }

        Box(modifier = Modifier
            .weight(1f)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = ButtonBorderColor,
                shape = AbsoluteCutCornerShape(topLeft = 16.dp)
            )
            .background(
                color = Color.Black,
                shape = AbsoluteCutCornerShape(topLeft = 16.dp)
            )
        ) {

            val prediction = predictions.values.toList()[0]

            val text = (
                    "Location: ${mainActivity.getCountry(mainActivity.displayLocation.value)} " +
                    "(lat:${mainActivity.displayLocation.value.latitude},long:${mainActivity.displayLocation.value.longitude})" +
                    "\n" + prediction.featureImportance +
                    "\n" + mainActivity.results.value.refinedSummary +
                    "\n\n"
                    )
                .replace("*", "")
                .replace("#", "")

            // Iterate over the characters.
            val breakIterator = remember(text) { BreakIterator.getCharacterInstance() }

            // Define the duration (milliseconds) of the pause before each successive
            // character is displayed. These pauses between characters create the
            // illusion of an animation.
            val typingDelayInMs = 1L

            var substringText = remember { mutableStateOf("") }
            LaunchedEffect(text) {
                // Initial start delay of the typing animation
                delay(200)
                breakIterator.text = StringCharacterIterator(text)

                var nextIndex = breakIterator.next()
                // Iterate over the string, by index boundary
                while (nextIndex != BreakIterator.DONE) {
                    substringText.value = text.subSequence(0, nextIndex).toString()
                    // Go to the next logical character boundary
                    nextIndex = breakIterator.next()
                    delay(typingDelayInMs)
                }
            }
            Text(
                text = substringText.value,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                maxLines = 1000,
                color = TextBlue,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )

        }

        Spacer(modifier = Modifier.padding(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
                .border(width = 2.dp, shape = RoundedCornerShape(8.dp), color = ButtonBorderColor)
                .background(color = ButtonColor, shape = RoundedCornerShape(8.dp))
                .clickable { navToLanding.invoke() },
        ){
            Text(
                text = stringResource(R.string.done),
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

val colors = intArrayOf(
    ColorTemplate.VORDIPLOM_COLORS[0],
    ColorTemplate.VORDIPLOM_COLORS[1],
    ColorTemplate.VORDIPLOM_COLORS[2]
)
