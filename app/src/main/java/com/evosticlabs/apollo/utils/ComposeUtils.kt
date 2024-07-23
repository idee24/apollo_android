package com.evosticlabs.apollo.utils

import android.content.Context
import android.graphics.drawable.shapes.OvalShape
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RatingBar
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.DialogProperties
import com.evosticlabs.apollo.R
import com.evosticlabs.apollo.ui.theme.ActionBlue
import com.evosticlabs.apollo.ui.theme.ButtonBorderColor
import com.evosticlabs.apollo.ui.theme.ButtonTextColor
import com.evosticlabs.apollo.ui.theme.DormantBlue
import com.evosticlabs.apollo.ui.theme.ErrorRed
import com.evosticlabs.apollo.ui.theme.FieldColor
import com.evosticlabs.apollo.ui.theme.TextBlue
import com.evosticlabs.apollo.ui.theme.TextColorDisabled

/**
 *@Created by Yerimah on 29/04/2023.
 */


@Composable
fun SignUpOptionCard(
    image: Int,
    onClick: () -> Unit
) {

    Box (
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick.invoke() }
    ) {
        Image(
            painter = painterResource(id = image),
            "Third Party Authentication",
            modifier = Modifier
                .height(50.dp)
                .width(50.dp),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun PageIndicator(
    numberOfPages: Int,
    modifier: Modifier = Modifier,
    selectedPage: Int = 0,
    selectedColor: Color = Color.Blue,
    defaultColor: Color = Color.LightGray,
    defaultRadius: Dp = 20.dp,
    selectedLength: Dp = 60.dp,
    space: Dp = 30.dp,
    animationDurationInMillis: Int = 300,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space),
        modifier = modifier,
    ) {
        for (i in 0 until numberOfPages) {
            val isSelected = i == selectedPage
            PageIndicatorView(
                isSelected = isSelected,
                selectedColor = selectedColor,
                defaultColor = defaultColor,
                defaultRadius = defaultRadius,
                selectedLength = selectedLength,
                animationDurationInMillis = animationDurationInMillis,
            )
        }
    }
}

@Composable
fun PageIndicatorView(
    isSelected: Boolean,
    selectedColor: Color,
    defaultColor: Color,
    defaultRadius: Dp,
    selectedLength: Dp,
    animationDurationInMillis: Int,
    modifier: Modifier = Modifier,
) {

    val color: Color by animateColorAsState(
        targetValue = if (isSelected) {
            selectedColor
        } else {
            defaultColor
        },
        animationSpec = tween(
            durationMillis = animationDurationInMillis,
        )
    )
    val width: Dp by animateDpAsState(
        targetValue = if (isSelected) {
            selectedLength
        } else {
            defaultRadius
        },
        animationSpec = tween(
            durationMillis = animationDurationInMillis,
        )
    )

    Canvas(
        modifier = modifier
            .size(
                width = width,
                height = defaultRadius,
            ),
    ) {
        drawRoundRect(
            color = color,
            topLeft = Offset.Zero,
            size = Size(
                width = width.toPx(),
                height = defaultRadius.toPx(),
            ),
            cornerRadius = CornerRadius(
                x = defaultRadius.toPx(),
                y = defaultRadius.toPx(),
            ),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: ((String) -> Unit),
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)
    LocalAutofillTree.current += autofillNode

    this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}
@Composable
fun ComposeWebView(modifier: Modifier = Modifier, context: Context, url: String) {

    AndroidView(modifier = modifier.fillMaxSize(),
        factory = {
            WebView(context).apply {
                webViewClient = WebViewClient()
                this.settings.apply {
                    javaScriptEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = true
                }
                loadUrl(url)
            }
        })
}


@Composable
fun HintText(hint: String, isError: Boolean) {
    Text(
        text = hint,
        color = TextColorDisabled,
        fontSize = 13.sp,
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomSpinner(
    modifier: Modifier = Modifier,
    items: List<String>,
    initialSelection: Int,
    onItemSelected: (Int) -> Unit
) {

    val expanded = remember { mutableStateOf(false) }
    val selectedOptionText = remember { mutableStateOf(initialSelection) }

    androidx.compose.material.ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        }
    ) {
        TextField(
            readOnly = true,
            modifier = modifier.fillMaxWidth(),
            value = items[selectedOptionText.value],
            onValueChange = { },
            trailingIcon = {
                androidx.compose.material.ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded.value
                )
            },
            colors = androidx.compose.material.ExposedDropdownMenuDefaults.textFieldColors(
                backgroundColor = FieldColor,
                cursorColor = ButtonTextColor,
                textColor = ButtonTextColor,
                focusedIndicatorColor = Color.Transparent,
                errorLeadingIconColor = ErrorRed,
                trailingIconColor = ButtonTextColor,
                focusedTrailingIconColor = ButtonTextColor

            )
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth().background(color = FieldColor),
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    modifier = Modifier.background(color = FieldColor),
                    onClick = {
                        selectedOptionText.value = items.indexOf(selectionOption)
                        expanded.value = false
                        onItemSelected.invoke(items.indexOf(selectionOption))
                    }
                ) {
                    Column(modifier = Modifier.fillMaxWidth().background(color = FieldColor)) {
                        androidx.compose.material.Text(
                            text = selectionOption,
                            style = androidx.compose.material.MaterialTheme.typography.body1,
                            color = ButtonTextColor
                        )

                        Divider(thickness = 1.dp, color = ButtonBorderColor)
                    }

                }
            }
        }
    }
}

