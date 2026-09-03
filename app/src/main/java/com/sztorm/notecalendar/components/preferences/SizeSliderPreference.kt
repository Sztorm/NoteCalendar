package com.sztorm.notecalendar.components.preferences

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sztorm.notecalendar.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <S> SizeSliderPreference(
    title: String,
    sizes: List<S>,
    selectedIndex: Int,
    onSizeChange: (Int, S) -> Unit,
    modifier: Modifier = Modifier,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    iconColor: Color = Color.Unspecified,
    sliderColors: SliderColors = SliderDefaults.colors(),
    enabled: Boolean = true,
) {
    Preference(
        title = title,
        onClick = {},
        modifier = modifier,
        paddingValues = PaddingValues(top = 16.dp),
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        isClickable = false,
        enabled = enabled
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    val index = max(0, selectedIndex - 1)
                    onSizeChange(index, sizes[index])
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_rounded_minus),
                    contentDescription = "-",
                    tint = iconColor
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            Slider(
                value = selectedIndex.toFloat(),
                onValueChange = { value ->
                    val index = value.roundToInt()
                    onSizeChange(index, sizes[index])
                },
                enabled = enabled,
                valueRange = 0f..sizes.lastIndex.toFloat(),
                steps = sizes.size - 2,
                colors = sliderColors,
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = remember { MutableInteractionSource() },
                        thumbSize = DpSize(12.dp, 12.dp),
                        enabled = enabled,
                        colors = sliderColors
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(2.dp),
                        thumbTrackGapSize = 0.dp,
                        trackInsideCornerSize = 0.dp,
                        enabled = enabled,
                        colors = sliderColors
                    )
                }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    val index = min(sizes.size - 1, selectedIndex + 1)
                    onSizeChange(index, sizes[index])
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_rounded_plus),
                    contentDescription = "+",
                    tint = iconColor
                )
            }
        }
    }
}