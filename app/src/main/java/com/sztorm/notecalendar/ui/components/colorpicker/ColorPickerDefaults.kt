package com.sztorm.notecalendar.ui.components.colorpicker

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable

object ColorPickerDefaults {
    private var defaultColorsCached: ColorPickerColors? = null
    private val defaultColors: ColorPickerColors
        @Composable
        get() {
            val cached = defaultColorsCached

            if (cached != null) return cached
            else {
                val result = ColorPickerColors(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    labelColor = MaterialTheme.colorScheme.onBackground,
                    tabButtonColor = MaterialTheme.colorScheme.primary,
                    iconButtonColor = MaterialTheme.colorScheme.onBackground,
                    textFieldColors = OutlinedTextFieldDefaults.colors(),
                    sliderColors = SliderDefaults.colors(),
                    segmentedButtonColors = SegmentedButtonDefaults.colors()
                )
                defaultColorsCached = result

                return result
            }
        }

    @Composable
    fun colors() = defaultColors
}