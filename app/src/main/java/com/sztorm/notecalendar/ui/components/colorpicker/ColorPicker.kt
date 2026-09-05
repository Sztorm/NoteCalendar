package com.sztorm.notecalendar.ui.components.colorpicker

import android.content.ClipData
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults.outlinedIconButtonColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sztorm.mathkit.ColorRGBA32
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.itemsSequence
import kotlinx.coroutines.launch
import java.text.DecimalFormatSymbols
import kotlin.math.min
import kotlin.math.roundToInt

fun parseHexCodeOrNull(hexCode: CharSequence): ColorRGBA32? {
    fun isHexNumberCharacter(char: Char) =
        char in '0'..'9' || char in 'a'..'f' || char in 'A'..'F'

    fun CharSequence.twoHexDigitsToUByte(index: Int) = (
        (this[index].digitToInt(16) shl 4) + this[index + 1].digitToInt(16)
        ).toUByte()

    return when {
        hexCode.length == 7 &&
            hexCode[0] == '#' &&
            hexCode.drop(1).all(::isHexNumberCharacter) -> {
            val r = hexCode.twoHexDigitsToUByte(1)
            val g = hexCode.twoHexDigitsToUByte(3)
            val b = hexCode.twoHexDigitsToUByte(5)

            ColorRGBA32(r, g, b, 0xffu)
        }

        hexCode.length == 9 &&
            hexCode[0] == '#' &&
            hexCode.drop(1).all(::isHexNumberCharacter) -> {
            val r = hexCode.twoHexDigitsToUByte(1)
            val g = hexCode.twoHexDigitsToUByte(3)
            val b = hexCode.twoHexDigitsToUByte(5)
            val a = hexCode.twoHexDigitsToUByte(7)

            ColorRGBA32(r, g, b, a)
        }

        else -> null
    }
}

private val ColorPickerTab.route
    get() = when (this) {
        is ColorPickerTab.ColorCodes -> "colorcodes"
        is ColorPickerTab.Hsl -> "hsl"
        is ColorPickerTab.Hsv -> "hsv"
        is ColorPickerTab.Rgb -> "rgb"
    }

private fun ColorPickerTab.title(texts: ColorPickerTexts) = when (this) {
    is ColorPickerTab.ColorCodes -> texts.colorCodesTitle
    is ColorPickerTab.Hsl -> texts.hslTitle
    is ColorPickerTab.Hsv -> texts.hsvTitle
    is ColorPickerTab.Rgb -> texts.rgbTitle
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPicker(
    state: ColorPickerState,
    modifier: Modifier = Modifier,
    colors: ColorPickerColors = ColorPickerDefaults.colors(),
    properties: ColorPickerProperties = ColorPickerProperties()
) {
    val navController = rememberNavController()
    val initialTab = properties.tabs.first()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = colors.backgroundColor)
    ) {
        val selectedTab = properties.tabs[selectedTabIndex]

        when (selectedTab.pickerType) {
            ColorPickerType.HslSquare -> HslColorPicker(
                state = state,
                supportsAlphaPicking = selectedTab.supportsAlphaPicking,
                modifier = Modifier.focusable()
            )

            ColorPickerType.HsvTriangle -> HsvColorPicker(
                state = state,
                supportsAlphaPicking = selectedTab.supportsAlphaPicking,
                modifier = Modifier.focusable()
            )

            ColorPickerType.RgbCircle -> RgbColorPicker(
                state = state,
                supportsAlphaPicking = selectedTab.supportsAlphaPicking,
                modifier = Modifier.focusable()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            divider = { HorizontalDivider(color = colors.textFieldColors.unfocusedIndicatorColor) }
        ) {
            properties.tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(tab.route)
                        selectedTabIndex = index
                    },
                    text = {
                        Text(
                            text = tab.title(properties.texts),
                            overflow = TextOverflow.Ellipsis,
                            color = colors.tabButtonColor
                        )
                    }
                )
            }
        }
        NavHost(
            navController = navController,
            startDestination = initialTab.route
        ) {
            properties.tabs.forEach { tab ->
                composable(tab.route) {
                    when (tab) {
                        is ColorPickerTab.ColorCodes -> ColorCodesTab(state, colors, tab)
                        is ColorPickerTab.Hsl -> HslTab(state, colors, properties, tab)
                        is ColorPickerTab.Hsv -> HsvTab(state, colors, properties, tab)
                        is ColorPickerTab.Rgb -> RgbTab(state, colors, properties, tab)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorCodeRow(
    value: String,
    onPaste: (ClipData) -> Unit,
    colors: ColorPickerColors,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            colors = colors.textFieldColors,
            modifier = Modifier.weight(1f)
        )
        OutlinedIconButton(
            onClick = {
                scope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(ClipData.newPlainText("color hex code", value))
                    )
                }
            },
            border = BorderStroke(width = 1.dp, color = colors.iconButtonColor),
            colors = outlinedIconButtonColors(
                contentColor = colors.tabButtonColor,
            ),
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Icon(
                imageVector = ImageVector
                    .vectorResource(R.drawable.icon_outline_content_copy),
                contentDescription = "copy",
                tint = colors.iconButtonColor
            )
        }
        OutlinedIconButton(
            onClick = {
                scope.launch {
                    clipboardManager.getClipEntry()?.let {
                        onPaste(it.clipData)
                    }
                }
            },
            border = BorderStroke(width = 1.dp, color = colors.iconButtonColor),
            colors = outlinedIconButtonColors(
                contentColor = colors.tabButtonColor,
            ),
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Icon(
                imageVector = ImageVector
                    .vectorResource(R.drawable.icon_outline_content_paste),
                contentDescription = "paste",
                tint = colors.iconButtonColor
            )
        }
    }
}

@Composable
private fun ColorCodesTab(
    state: ColorPickerState, colors: ColorPickerColors, tab: ColorPickerTab.ColorCodes
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        tab.codes.forEach { code ->
            ColorCodeRow(
                value = code.toString(state),
                onPaste = { clipData ->
                    clipData
                        .itemsSequence()
                        .map { code.parseOrNull(it.text) }
                        .firstOrNull()
                        ?.let { code.onPaste(it, state) }
                },
                colors = colors,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun Float.formatUpToTwoDecimalPlaces(): String {
    val locale = Locale.current.platformLocale
    val result = "%.2f".format(locale, this)
    val separator = DecimalFormatSymbols(locale).decimalSeparator

    return when {
        result.endsWith(separator + "00") -> result.dropLast(3)
        result.endsWith(separator + "0") -> result.dropLast(2)
        result.endsWith('0') -> result.dropLast(1)
        else -> result
    }
}

private fun Float.formatToInteger(): String = "%.0f".format(this)

@Composable
private fun ColorComponentSlider(
    text: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    format: (Float) -> String = { it.toString() },
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textColor: Color = Color.Unspecified,
    textFieldColors: TextFieldColors,
    sliderColors: SliderColors
) = ColorComponentSlider(
    text = text,
    value = value,
    toString = format,
    stringToT = { it.toFloatOrNull() },
    toFloat = { it },
    floatToT = { it },
    valueRange = valueRange,
    onValueChange = onValueChange,
    prefix = prefix,
    suffix = suffix,
    textColor = textColor,
    textFieldColors = textFieldColors,
    sliderColors = sliderColors
)

@Composable
private fun ColorComponentSlider(
    text: String,
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textColor: Color = Color.Unspecified,
    textFieldColors: TextFieldColors,
    sliderColors: SliderColors
) = ColorComponentSlider(
    text = text,
    value = value,
    toString = { it.toString() },
    stringToT = { it.toIntOrNull() },
    toFloat = { it.toFloat() },
    floatToT = { it.roundToInt() },
    valueRange = valueRange,
    onValueChange = onValueChange,
    steps = valueRange.last + 1 - valueRange.first,
    prefix = prefix,
    suffix = suffix,
    textColor = textColor,
    textFieldColors = textFieldColors,
    sliderColors = sliderColors.copy(
        activeTickColor = Color.Transparent,
        disabledActiveTickColor = Color.Transparent,
        disabledInactiveTickColor = Color.Transparent,
        inactiveTickColor = Color.Transparent,
    )
)

@Composable
private inline fun <reified T : Comparable<T>> ColorComponentSlider(
    text: String,
    value: T,
    crossinline toString: (T) -> String,
    crossinline stringToT: (String) -> T?,
    crossinline toFloat: (T) -> Float,
    crossinline floatToT: (Float) -> T,
    valueRange: ClosedRange<T>,
    noinline onValueChange: (T) -> Unit,
    steps: Int = 0,
    noinline prefix: @Composable (() -> Unit)? = null,
    noinline suffix: @Composable (() -> Unit)? = null,
    textColor: Color = Color.Unspecified,
    textFieldColors: TextFieldColors,
    sliderColors: SliderColors
) {
    var textState by remember(value) {
        mutableStateOf(
            TextFieldValue(
                annotatedString = AnnotatedString(text = toString(value))
            )
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = textColor
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Slider(
                    value = toFloat(value),
                    onValueChange = { onValueChange(floatToT(it)) },
                    valueRange = toFloat(valueRange.start)..toFloat(valueRange.endInclusive),
                    steps = steps,
                    colors = sliderColors
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.width(85.dp * LocalDensity.current.fontScale)) {
                val focusManager = LocalFocusManager.current

                OutlinedTextField(
                    value = textState,
                    onValueChange = {
                        textState = it.copy(
                            annotatedString = AnnotatedString(
                                text = it.text.substring(0, min(6, it.text.length))
                            )
                        )
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            stringToT(textState.text).let {
                                when (it) {
                                    null -> textState = TextFieldValue(
                                        annotatedString = AnnotatedString(text = toString(value))
                                    )

                                    else -> onValueChange(it.coerceIn(valueRange))
                                }
                            }
                            focusManager.clearFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Decimal,
                    ),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    colors = textFieldColors,
                    prefix = prefix,
                    suffix = suffix,
                    modifier = Modifier.onFocusChanged {
                        textState = TextFieldValue(
                            annotatedString = AnnotatedString(text = toString(value))
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun RgbTab(
    state: ColorPickerState,
    colors: ColorPickerColors,
    properties: ColorPickerProperties,
    tab: ColorPickerTab.Rgb
) {
    var selectedFormatIndex by remember {
        mutableIntStateOf(tab.defaultValuesFormat.ordinal)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (tab.supportsFormatPicking) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                RgbValuesFormat.entries.forEachIndexed { index, format ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = RgbValuesFormat.entries.size
                        ),
                        onClick = { selectedFormatIndex = index },
                        selected = index == selectedFormatIndex,
                        label = { Text(format.label) },
                        colors = colors.segmentedButtonColors
                    )
                }
            }
        }
        when (RgbValuesFormat.entries[selectedFormatIndex]) {
            RgbValuesFormat.Integer -> {
                Row {
                    ColorComponentSlider(
                        text = properties.texts.red,
                        value = (state.rgb.red * 255f).roundToInt(),
                        valueRange = 0..255,
                        onValueChange = { state.rgb = state.rgb.copy(red = it.toFloat() / 255f) },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.green,
                        value = (state.rgb.green * 255f).roundToInt(),
                        valueRange = 0..255,
                        onValueChange = { state.rgb = state.rgb.copy(green = it.toFloat() / 255f) },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.blue,
                        value = (state.rgb.blue * 255).roundToInt(),
                        valueRange = 0..255,
                        onValueChange = { state.rgb = state.rgb.copy(blue = it.toFloat() / 255f) },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                if (tab.supportsAlphaPicking) {
                    Row {
                        ColorComponentSlider(
                            text = properties.texts.alpha,
                            value = (state.alpha * 255).roundToInt(),
                            valueRange = 0..255,
                            onValueChange = { state.alpha = it.toFloat() / 255f },
                            textColor = colors.labelColor,
                            sliderColors = colors.sliderColors,
                            textFieldColors = colors.textFieldColors
                        )
                    }
                }
            }

            RgbValuesFormat.FloatingPoint -> {
                Row {
                    ColorComponentSlider(
                        text = properties.texts.red,
                        value = state.rgb.red,
                        valueRange = 0f..1f,
                        onValueChange = { state.rgb = state.rgb.copy(red = it) },
                        format = { it.formatUpToTwoDecimalPlaces() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.green,
                        value = state.rgb.green,
                        valueRange = 0f..1f,
                        onValueChange = { state.rgb = state.rgb.copy(green = it) },
                        format = { it.formatUpToTwoDecimalPlaces() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.blue,
                        value = state.rgb.blue,
                        valueRange = 0f..1f,
                        onValueChange = { state.rgb = state.rgb.copy(blue = it) },
                        format = { it.formatUpToTwoDecimalPlaces() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                if (tab.supportsAlphaPicking) {
                    Row {
                        ColorComponentSlider(
                            text = properties.texts.alpha,
                            value = state.alpha,
                            valueRange = 0f..1f,
                            onValueChange = { state.alpha = it },
                            format = { it.formatUpToTwoDecimalPlaces() },
                            textColor = colors.labelColor,
                            sliderColors = colors.sliderColors,
                            textFieldColors = colors.textFieldColors
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HsvTab(
    state: ColorPickerState,
    colors: ColorPickerColors,
    properties: ColorPickerProperties,
    tab: ColorPickerTab.Hsv
) {
    var selectedFormatIndex by remember {
        mutableIntStateOf(tab.defaultValuesFormat.ordinal)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (tab.supportsFormatPicking) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                HsvValuesFormat.entries.forEachIndexed { index, format ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = HsvValuesFormat.entries.size
                        ),
                        onClick = { selectedFormatIndex = index },
                        selected = index == selectedFormatIndex,
                        label = { Text(format.label) },
                        colors = colors.segmentedButtonColors
                    )
                }
            }
        }
        when (HsvValuesFormat.entries[selectedFormatIndex]) {
            HsvValuesFormat.Standard -> {
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hsvHue,
                        value = state.hsv.hue,
                        valueRange = 0f..360f,
                        onValueChange = { state.hsv = state.hsv.copy(hue = it) },
                        format = { it.formatToInteger() },
                        suffix = { Text("°") },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hsvSaturation,
                        value = state.hsv.saturation * 100f,
                        valueRange = 0f..100f,
                        onValueChange = { state.hsv = state.hsv.copy(saturation = it * 0.01f) },
                        format = { it.formatToInteger() },
                        suffix = { Text("%") },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hsvValue,
                        value = state.hsv.value * 100f,
                        valueRange = 0f..100f,
                        onValueChange = { state.hsv = state.hsv.copy(value = it * 0.01f) },
                        format = { it.formatToInteger() },
                        suffix = { Text("%") },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                if (tab.supportsAlphaPicking) {
                    Row {
                        ColorComponentSlider(
                            text = properties.texts.alpha,
                            value = state.alpha * 100f,
                            valueRange = 0f..100f,
                            onValueChange = { state.alpha = it * 0.01f },
                            format = { it.formatToInteger() },
                            suffix = { Text("%") },
                            textColor = colors.labelColor,
                            sliderColors = colors.sliderColors,
                            textFieldColors = colors.textFieldColors
                        )
                    }
                }
            }

            HsvValuesFormat.FloatingPoint -> {
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hsvHue,
                        value = state.hsv.hue,
                        valueRange = 0f..360f,
                        onValueChange = { state.hsv = state.hsv.copy(hue = it) },
                        format = { it.formatToInteger() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hsvSaturation,
                        value = state.hsv.saturation,
                        valueRange = 0f..1f,
                        onValueChange = { state.hsv = state.hsv.copy(saturation = it) },
                        format = { it.formatUpToTwoDecimalPlaces() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hsvValue,
                        value = state.hsv.value,
                        valueRange = 0f..1f,
                        onValueChange = { state.hsv = state.hsv.copy(value = it) },
                        format = { it.formatUpToTwoDecimalPlaces() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                if (tab.supportsAlphaPicking) {
                    Row {
                        ColorComponentSlider(
                            text = properties.texts.alpha,
                            value = state.alpha,
                            valueRange = 0f..1f,
                            onValueChange = { state.alpha = it },
                            format = { it.formatUpToTwoDecimalPlaces() },
                            textColor = colors.labelColor,
                            sliderColors = colors.sliderColors,
                            textFieldColors = colors.textFieldColors
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HslTab(
    state: ColorPickerState,
    colors: ColorPickerColors,
    properties: ColorPickerProperties,
    tab: ColorPickerTab.Hsl
) {
    var selectedFormatIndex by remember {
        mutableIntStateOf(tab.defaultValuesFormat.ordinal)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (tab.supportsFormatPicking) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                HslValuesFormat.entries.forEachIndexed { index, format ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = HslValuesFormat.entries.size
                        ),
                        onClick = { selectedFormatIndex = index },
                        selected = index == selectedFormatIndex,
                        label = { Text(format.label) },
                        colors = colors.segmentedButtonColors
                    )
                }
            }
        }
        when (HslValuesFormat.entries[selectedFormatIndex]) {
            HslValuesFormat.Standard -> {
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hslHue,
                        value = state.hsl.hue,
                        valueRange = 0f..360f,
                        onValueChange = { state.hsl = state.hsl.copy(hue = it) },
                        format = { it.formatToInteger() },
                        suffix = { Text("°") },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hslSaturation,
                        value = state.hsl.saturation * 100f,
                        valueRange = 0f..100f,
                        onValueChange = { state.hsl = state.hsl.copy(saturation = it * 0.01f) },
                        format = { it.formatToInteger() },
                        suffix = { Text("%") },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hslLightness,
                        value = state.hsl.lightness * 100f,
                        valueRange = 0f..100f,
                        onValueChange = { state.hsl = state.hsl.copy(lightness = it * 0.01f) },
                        format = { it.formatToInteger() },
                        suffix = { Text("%") },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                if (tab.supportsAlphaPicking) {
                    Row {
                        ColorComponentSlider(
                            text = properties.texts.alpha,
                            value = state.alpha * 100f,
                            valueRange = 0f..100f,
                            onValueChange = { state.alpha = it * 0.01f },
                            format = { it.formatToInteger() },
                            suffix = { Text("%") },
                            textColor = colors.labelColor,
                            sliderColors = colors.sliderColors,
                            textFieldColors = colors.textFieldColors
                        )
                    }
                }
            }

            HslValuesFormat.FloatingPoint -> {
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hslHue,
                        value = state.hsl.hue,
                        valueRange = 0f..360f,
                        onValueChange = { state.hsl = state.hsl.copy(hue = it) },
                        format = { it.formatToInteger() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hslSaturation,
                        value = state.hsl.saturation,
                        valueRange = 0f..1f,
                        onValueChange = { state.hsl = state.hsl.copy(saturation = it) },
                        format = { it.formatUpToTwoDecimalPlaces() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                Row {
                    ColorComponentSlider(
                        text = properties.texts.hslLightness,
                        value = state.hsl.lightness,
                        valueRange = 0f..1f,
                        onValueChange = { state.hsl = state.hsl.copy(lightness = it) },
                        format = { it.formatUpToTwoDecimalPlaces() },
                        textColor = colors.labelColor,
                        sliderColors = colors.sliderColors,
                        textFieldColors = colors.textFieldColors
                    )
                }
                if (tab.supportsAlphaPicking) {
                    Row {
                        ColorComponentSlider(
                            text = properties.texts.alpha,
                            value = state.alpha,
                            valueRange = 0f..1f,
                            onValueChange = { state.alpha = it },
                            format = { it.formatUpToTwoDecimalPlaces() },
                            textColor = colors.labelColor,
                            sliderColors = colors.sliderColors,
                            textFieldColors = colors.textFieldColors
                        )
                    }
                }
            }
        }
    }
}