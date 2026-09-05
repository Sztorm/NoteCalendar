package com.sztorm.notecalendar.ui.components.colorpicker

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sztorm.mathkit.AngleF
import com.sztorm.mathkit.ComplexF
import com.sztorm.mathkit.Vector2F
import com.sztorm.mathkit.euclidean2d.Annulus
import com.sztorm.mathkit.euclidean2d.Circle
import com.sztorm.mathkit.euclidean2d.MutableAnnulus
import com.sztorm.mathkit.euclidean2d.MutableCircle
import com.sztorm.mathkit.euclidean2d.MutableRegularTriangle
import com.sztorm.mathkit.euclidean2d.MutableRoundedRectangle
import com.sztorm.mathkit.euclidean2d.MutableSquare
import com.sztorm.mathkit.euclidean2d.RegularTriangle
import com.sztorm.mathkit.euclidean2d.RoundedRectangle
import com.sztorm.mathkit.euclidean2d.Square
import com.sztorm.mathkit.inverseLerp
import com.sztorm.mathkit.lerp
import com.sztorm.notecalendar.core.common.isEven
import com.sztorm.notecalendar.core.common.isOdd
import com.sztorm.notecalendar.core.common.lineTo
import com.sztorm.notecalendar.core.common.moveTo
import com.sztorm.notecalendar.core.common.toCanvasSpace
import com.sztorm.notecalendar.core.common.toOffset
import com.sztorm.notecalendar.core.common.toVector2F
import kotlin.math.abs
import kotlin.math.sqrt

private enum class CurrentAction {
    None,
    ChangingHue,
    ChangingSV,
    ChangingSL,
    ChangingColor,
    ChangingValue,
    ChangingAlpha
}

val LightGray = Color(0.3333333f, 0.3333333f, 0.3333333f)
val DarkGray = Color(0.666667f, 0.6666667f, 0.6666667f)

private fun drawWithCompositeBrush(
    vararg brushBlendModePairs: Pair<Brush, BlendMode>,
    drawFunction: (Pair<Brush, BlendMode>) -> Unit
) = when {
    brushBlendModePairs.isEmpty() -> {}

    Build.VERSION.SDK_INT >= 29 -> {
        var (finalBrush, initialBlendMode) = brushBlendModePairs.first()

        for (i in 1 until brushBlendModePairs.size) {
            val (brush, blendMode) = brushBlendModePairs[i]
            finalBrush = finalBrush
                .let { Brush.composite(it, brush, blendMode) }
        }
        drawFunction(Pair(finalBrush, initialBlendMode))
    }

    else -> brushBlendModePairs.forEach {
        drawFunction(it)
    }
}

private fun triangleAreaDoubled(a: Vector2F, b: Vector2F, c: Vector2F) =
    abs(a.x * b.y - b.x * a.y + b.x * c.y - c.x * b.y + c.x * a.y - a.x * c.y)

private fun triagleHeightOfBaseAB(a: Vector2F, b: Vector2F, c: Vector2F): Float {
    val areaDoubled = triangleAreaDoubled(a, b, c)
    val baseLength = a.distanceTo(b)

    return areaDoubled / baseLength
}

private fun Annulus.hueFrom(position: Vector2F) = (position - center)
    .toComplexF()
    .normalizedOrElse { ComplexF.ONE }
    .phaseAngle
    .getMinimalPositiveCoterminal()
    .degrees
    .coerceIn(0f, 360f)

private fun RegularTriangle.colorSaturationFrom(position: Vector2F): Float {
    val abpAreaDoubled = triangleAreaDoubled(pointA, pointB, position)
    val cbpAreaDoubled = triangleAreaDoubled(pointC, pointB, position)
    val abcpAreaDoubled = abpAreaDoubled + cbpAreaDoubled

    return when {
        abcpAreaDoubled < 0.1f -> 0.5f
        else -> {
            val result = (cbpAreaDoubled / abcpAreaDoubled).coerceIn(0f, 1f)

            when {
                result < 0.01f -> 0f
                result > 0.99f -> 1f
                else -> result
            }
        }
    }
}

private fun RegularTriangle.valueFrom(position: Vector2F): Float {
    val triangleHeight = circumradius + inradius
    val height = triagleHeightOfBaseAB(pointA, pointC, position)
    val result = (1f - height / triangleHeight).coerceIn(0f, 1f)

    return when {
        result < 0.01f -> 0f
        result > 0.99f -> 1f
        else -> result
    }
}

private fun RegularTriangle.colorPosition(saturation: Float, value: Float): Vector2F =
    lerp(pointB, lerp(pointC, pointA, saturation), value)

private fun Square.colorSaturationFrom(position: Vector2F): Float {
    val abpHeight = triagleHeightOfBaseAB(pointA, pointB, position)

    return (1f - abpHeight / sideLength).coerceIn(0f, 1f)
}

private fun Square.colorLightnessFrom(position: Vector2F): Float {
    val apdHeight = triagleHeightOfBaseAB(pointA, pointD, position)

    return (apdHeight / sideLength).coerceIn(0f, 1f)
}

private fun Square.colorPosition(saturation: Float, lightness: Float) = lerp(
    lerp(pointD, pointC, lightness),
    lerp(pointA, pointB, lightness),
    saturation
)

private fun Circle.hueFrom(position: Vector2F): Float = (position - center)
    .toComplexF()
    .normalizedOrElse { ComplexF.ONE }
    .phaseAngle
    .getMinimalPositiveCoterminal()
    .degrees
    .coerceIn(0f, 360f)

private fun Circle.saturationFrom(position: Vector2F) =
    (center.distanceTo(position) / radius).coerceIn(0f, 1f)

private fun Circle.colorPosition(hue: Float, saturation: Float) = ComplexF.fromPolar(
    magnitude = saturation * radius,
    phase = AngleF.fromDegrees(hue).radians
).toVector2F() + center

private fun RoundedRectangle.valueFrom(position: Vector2F) =
    inverseLerp(cornerCenterB.x, cornerCenterA.x, position.x).coerceIn(0f, 1f)

private fun RoundedRectangle.valuePosition(value: Float) =
    lerp(cornerCenterB, cornerCenterA, value)

private fun DrawScope.drawHueRing(ring: Annulus) {
    val canvasSize = size
    val radius = (ring.innerRadius + ring.outerRadius) * 0.5f
    val stroke = Stroke(width = ring.width)
    val hueGradient = Brush.sweepGradient(
        listOf(
            Color.Red,
            Color.Magenta,
            Color.Blue,
            Color.Cyan,
            Color.Green,
            Color.Yellow,
            Color.Red,
        ),
        ring.center.toCanvasSpace(canvasSize).toOffset()
    )
    drawCircle(
        brush = hueGradient,
        radius = radius,
        style = stroke,
        center = ring.center.toCanvasSpace(canvasSize).toOffset(),
    )
}

private fun DrawScope.drawHueSelector(
    selectorTriangle: RegularTriangle,
    hueRing: Annulus,
    strokeWidth: Float,
    hue: Float
) {
    val canvasSize = size
    val pointA = selectorTriangle.pointA.toCanvasSpace(canvasSize)
    val pointB = selectorTriangle.pointB.toCanvasSpace(canvasSize)
    val pointC = selectorTriangle.pointC.toCanvasSpace(canvasSize)
    val heightRatio = ((hueRing.outerRadius - hueRing.innerRadius) / hueRing.outerRadius) * 1.01f
    val path = Path().apply {
        arcTo(
            rect = Rect(
                center = hueRing.center.toCanvasSpace(canvasSize).toOffset(),
                radius = hueRing.outerRadius * 0.995f
            ),
            startAngleDegrees = -hue - 30f * heightRatio,
            sweepAngleDegrees = 60f * heightRatio,
            forceMoveTo = true
        )
        moveTo(pointB)
        lineTo(pointA)
        lineTo(pointC)
    }
    drawPath(
        path = path,
        color = Color.White,
        style = Stroke(width = strokeWidth)
    )
}

private fun DrawScope.drawHsvTriangle(triangle: RegularTriangle, hue: Float) {
    val canvasSize = size
    val pointA = triangle.pointA.toCanvasSpace(canvasSize)
    val pointB = triangle.pointB.toCanvasSpace(canvasSize)
    val pointC = triangle.pointC.toCanvasSpace(canvasSize)
    val pointAOpposite = (pointB + pointC) * 0.5f
    val pointBOpposite = (pointA + pointC) * 0.5f
    val hueColor = Color.hsv(hue, saturation = 1f, value = 1f)
    val hueToTransparentGradient = Brush.linearGradient(
        listOf(hueColor, hueColor.copy(alpha = 0f)),
        start = pointA.toOffset(),
        end = pointAOpposite.toOffset()
    )
    val blackToTransparentGradient = Brush.linearGradient(
        listOf(Color.Black, Color.Transparent),
        start = pointB.toOffset(),
        end = pointBOpposite.toOffset()
    )
    val trianglePath = Path().apply {
        moveTo(pointA)
        lineTo(pointB)
        lineTo(pointC)
        close()
    }
    drawWithCompositeBrush(
        SolidColor(Color.White) to BlendMode.SrcOver,
        hueToTransparentGradient to BlendMode.SrcOver,
        blackToTransparentGradient to BlendMode.SrcOver,
    ) { (brush, blendMode) ->
        drawPath(
            path = trianglePath,
            brush = brush,
            blendMode = blendMode
        )
    }
}

private fun DrawScope.drawHslSquare(square: Square, hue: Float) {
    val canvasSize = size
    val pointA = square.pointA.toCanvasSpace(canvasSize)
    val pointB = square.pointB.toCanvasSpace(canvasSize)
    val pointC = square.pointC.toCanvasSpace(canvasSize)
    val pointD = square.pointD.toCanvasSpace(canvasSize)
    val hueColor = Color.hsv(hue, saturation = 1f, value = 1f)
    val hueToGrayGradient = Brush.linearGradient(
        listOf(hueColor, Color.Gray),
        start = ((pointA + pointB) * 0.5f).toOffset(),
        end = ((pointC + pointD) * 0.5f).toOffset()
    )
    val whiteToBlackGradient = Brush.linearGradient(
        listOf(Color.White, Color.Transparent, Color.Black),
        start = ((pointB + pointC) * 0.5f).toOffset(),
        end = ((pointD + pointA) * 0.5f).toOffset(),
    )
    val squarePath = Path().apply {
        moveTo(pointD)
        lineTo(pointA)
        lineTo(pointB)
        lineTo(pointC)
        close()
    }
    drawWithCompositeBrush(
        hueToGrayGradient to BlendMode.SrcOver,
        whiteToBlackGradient to BlendMode.SrcOver,
    ) { (brush, blendMode) ->
        drawPath(
            path = squarePath,
            brush = brush,
            blendMode = blendMode
        )
    }
}

private fun DrawScope.drawRgbCircle(circle: Circle) {
    val canvasSize = size
    val hueGradient = Brush.sweepGradient(
        colors = listOf(
            Color.Red,
            Color.Magenta,
            Color.Blue,
            Color.Cyan,
            Color.Green,
            Color.Yellow,
            Color.Red,
        ),
        center = circle.center.toCanvasSpace(canvasSize).toOffset()
    )
    val whiteGradient = Brush.radialGradient(
        colors = listOf(Color.White, Color.Transparent),
        center = circle.center.toCanvasSpace(canvasSize).toOffset()
    )
    drawWithCompositeBrush(
        hueGradient to BlendMode.SrcOver,
        whiteGradient to BlendMode.SrcOver,
    ) { (brush, blendMode) ->
        drawCircle(
            brush = brush,
            radius = circle.radius,
            center = circle.center.toCanvasSpace(canvasSize).toOffset(),
            blendMode = blendMode
        )
    }
}

private fun DrawScope.drawColorValueRectangle(
    rectangle: RoundedRectangle, state: ColorPickerState
) {
    val canvasSize = size
    val sameColorFraction = rectangle.cornerRadius / rectangle.width
    val valueColor = state.hsv.copy(value = 1f).toColor()
    val valueGradient = Brush.horizontalGradient(
        0f to Color.Black,
        sameColorFraction to Color.Black,
        1f - sameColorFraction to valueColor,
        1f to valueColor
    )
    drawRoundRect(
        brush = valueGradient,
        topLeft = Vector2F(rectangle.pointC.x, rectangle.pointB.y)
            .toCanvasSpace(canvasSize).toOffset(),
        size = Size(rectangle.width, rectangle.height),
        cornerRadius = rectangle.cornerRadius.let { CornerRadius(it, it) }
    )
}

private fun DrawScope.drawAlphaRectangle(
    rectangle: RoundedRectangle, state: ColorPickerState
) {
    val canvasSize = size
    val topLeft = Vector2F(rectangle.pointC.x, rectangle.pointB.y)
        .toCanvasSpace(canvasSize)
        .toOffset()
    val cornerRadius = rectangle.cornerRadius.let { CornerRadius(it, it) }
    val rectSize = Size(rectangle.width, rectangle.height)
    val path = Path().apply {
        moveTo(rectangle.pointH.toCanvasSpace(canvasSize))
        arcTo(
            rect = Rect(
                rectangle.cornerCenterA.toCanvasSpace(canvasSize).toOffset(),
                radius = rectangle.cornerRadius
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = -90f,
            forceMoveTo = false
        )
        lineTo(rectangle.pointA.toCanvasSpace(canvasSize))
        lineTo(rectangle.pointB.toCanvasSpace(canvasSize))
        arcTo(
            rect = Rect(
                rectangle.cornerCenterB.toCanvasSpace(canvasSize).toOffset(),
                radius = rectangle.cornerRadius
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = -90f,
            forceMoveTo = false
        )
        lineTo(rectangle.pointC.toCanvasSpace(canvasSize))
        lineTo(rectangle.pointD.toCanvasSpace(canvasSize))
        arcTo(
            rect = Rect(
                rectangle.cornerCenterC.toCanvasSpace(canvasSize).toOffset(),
                radius = rectangle.cornerRadius
            ),
            startAngleDegrees = -180f,
            sweepAngleDegrees = -90f,
            forceMoveTo = false
        )
        lineTo(rectangle.pointE.toCanvasSpace(canvasSize))
        lineTo(rectangle.pointF.toCanvasSpace(canvasSize))
        arcTo(
            rect = Rect(
                rectangle.cornerCenterD.toCanvasSpace(canvasSize).toOffset(),
                radius = rectangle.cornerRadius
            ),
            startAngleDegrees = -270f,
            sweepAngleDegrees = -90f,
            forceMoveTo = false
        )
        lineTo(rectangle.pointG.toCanvasSpace(canvasSize))
        lineTo(rectangle.pointH.toCanvasSpace(canvasSize))
        close()
    }
    val sameColorFraction = rectangle.cornerRadius / rectangle.width
    val color = state.rgb.toColor()
    val alphaGradient = Brush.horizontalGradient(
        0f to Color.Transparent,
        sameColorFraction to Color.Transparent,
        1f - sameColorFraction to color,
        1f to color
    )
    val alphaBoxSize = size.width * 0.03f
    val rows = (rectangle.height / alphaBoxSize).toInt()
    val cols = (rectangle.width / alphaBoxSize).toInt()
    val (initialX, initialY) = rectangle.pointC.toCanvasSpace(canvasSize) -
        Vector2F(0f, (rows + 1) * 0.5f * alphaBoxSize)

    drawRoundRect(
        color = LightGray,
        topLeft = topLeft,
        size = rectSize,
        cornerRadius = cornerRadius
    )
    clipPath(path) {
        for (row in 0..rows) {
            for (col in 0..cols) {
                val color =
                    if (row.isEven && col.isEven || row.isOdd && col.isOdd) LightGray
                    else DarkGray

                drawRect(
                    color = color,
                    topLeft = Offset(
                        initialX + col * alphaBoxSize, initialY + row * alphaBoxSize
                    ),
                    size = Size(alphaBoxSize, alphaBoxSize)
                )
            }
        }
    }
    drawRoundRect(
        brush = alphaGradient,
        topLeft = topLeft,
        size = rectSize,
        cornerRadius = cornerRadius
    )
}

private fun DrawScope.drawColorCircleSelector(
    position: Vector2F,
    radius: Float,
    strokeWidth: Float,
    state: ColorPickerState
) {
    val canvasSize = size
    val t = (lerp(1f, 0f, state.hsv.value) +
        lerp(0f, 1f, state.hsv.saturation))
        .coerceIn(0f, 1f)

    drawCircle(
        color = Color.hsv(0f, 0f, if (t > 0.5) 1f else 0f),
        radius = radius,
        center = position.toCanvasSpace(canvasSize).toOffset(),
        style = Stroke(width = strokeWidth)
    )
}

private fun DrawScope.drawAlphaCircleSelector(
    position: Vector2F,
    radius: Float,
    strokeWidth: Float,
    state: ColorPickerState
) {
    val canvasSize = size
    val t = when {
        state.alpha < 0.5f -> 1f
        else -> (lerp(1f, 0f, state.hsv.value) +
            lerp(0f, 1f, state.hsv.saturation))
            .coerceIn(0f, 1f)
    }
    drawCircle(
        color = Color.hsv(0f, 0f, if (t > 0.5) 1f else 0f),
        radius = radius,
        center = position.toCanvasSpace(canvasSize).toOffset(),
        style = Stroke(width = strokeWidth)
    )
}

private fun DrawScope.drawColorSelector(
    position: Vector2F,
    strokeWidth: Float,
    state: ColorPickerState,
    supportsAlphaPicking: Boolean
) {
    val canvasSize = size
    val radius = size.width * 0.04f
    val path = Path().apply {
        moveTo(position.toCanvasSpace(canvasSize))
        arcTo(
            rect = Rect(
                center = (position + Vector2F(0f, radius * 1.5f))
                    .toCanvasSpace(canvasSize).toOffset(),
                radius = radius
            ),
            startAngleDegrees = 30f,
            sweepAngleDegrees = -240f,
            forceMoveTo = false
        )
        close()
    }
    val t = (lerp(1f, 0f, state.hsv.value) +
        lerp(0f, 1f, state.hsv.saturation))
        .coerceIn(0f, 1f)

    if (supportsAlphaPicking) {
        val (initialX, initialY) =
            position.toCanvasSpace(canvasSize) - Vector2F(radius * 1.5f, radius * 3f)
        val alphaBoxSize = size.width * 0.03f
        val size = Size(radius * 2.5f, radius * 3f)
        val rows = (size.height / alphaBoxSize).toInt()
        val cols = (size.width / alphaBoxSize).toInt()

        clipPath(path) {
            drawRect(
                color = LightGray,
                topLeft = Offset(initialX, initialY),
                size = size
            )
            for (row in 0..rows) {
                for (col in 0..cols) {
                    val color =
                        if (row.isEven && col.isEven || row.isOdd && col.isOdd) LightGray
                        else DarkGray

                    drawRect(
                        color = color,
                        topLeft = Offset(
                            initialX + col * alphaBoxSize, initialY + row * alphaBoxSize
                        ),
                        size = Size(alphaBoxSize, alphaBoxSize)
                    )
                }
            }
            drawPath(
                path = path,
                alpha = state.alpha,
                color = state.rgb.toColor()
            )
        }
    } else {
        drawPath(
            path = path,
            color = state.rgb.toColor()
        )
    }
    drawPath(
        path = path,
        color = Color.hsv(0f, 0f, if (t > 0.5) 1f else 0f),
        style = Stroke(width = strokeWidth)
    )
}

@Composable
fun HsvColorPicker(
    state: ColorPickerState, supportsAlphaPicking: Boolean, modifier: Modifier = Modifier
) {
    var currentAction by remember { mutableStateOf(CurrentAction.None) }
    val hueRing = remember {
        MutableAnnulus(
            center = Vector2F.ZERO,
            orientation = ComplexF.ONE,
            innerRadius = 1f,
            outerRadius = 2f,
        )
    }
    val hueSelectorTriangle = remember {
        MutableRegularTriangle(
            center = Vector2F.ZERO,
            orientation = AngleF.fromDegrees(-90f).toComplexF(),
            sideLength = 100f
        )
    }
    val hsvTriangle = remember {
        MutableRegularTriangle(
            center = Vector2F.ZERO,
            orientation = AngleF.fromDegrees(-90f).toComplexF(),
            sideLength = 100f
        )
    }
    val alphaRectangle = remember {
        MutableRoundedRectangle(
            center = Vector2F.ZERO,
            orientation = ComplexF.ONE,
            width = 1f,
            height = 1f,
            cornerRadius = 0f
        )
    }
    var canvasSize by remember { mutableStateOf(Size(1f, 1f)) }
    val aspectRatio = if (supportsAlphaPicking) 0.9f else 1f
    val onInputStart = { startPosition: Offset ->
        val position = startPosition
            .toVector2F()
            .toCanvasSpace(canvasSize)

        when {
            position in hueRing -> {
                currentAction = CurrentAction.ChangingHue
                state.hsv = state.hsv.copy(hue = hueRing.hueFrom(position))
            }

            position in hsvTriangle -> {
                currentAction = CurrentAction.ChangingSV
                state.hsv = state.hsv.copy(
                    saturation = hsvTriangle.colorSaturationFrom(position),
                    value = hsvTriangle.valueFrom(position)
                )
            }

            supportsAlphaPicking && position in alphaRectangle -> {
                currentAction = CurrentAction.ChangingAlpha
                state.alpha = alphaRectangle.valueFrom(position)
            }
        }
    }
    val onInputUpdate = { change: PointerInputChange, _: Offset ->
        val position = change.position
            .toVector2F()
            .toCanvasSpace(canvasSize)

        when (currentAction) {
            CurrentAction.ChangingHue -> {
                state.hsv = state.hsv.copy(hue = hueRing.hueFrom(position))
            }

            CurrentAction.ChangingSV -> {
                val selectorPosition = hsvTriangle.closestPointTo(position)
                state.hsv = state.hsv.copy(
                    saturation = hsvTriangle.colorSaturationFrom(selectorPosition),
                    value = hsvTriangle.valueFrom(selectorPosition)
                )
            }

            CurrentAction.ChangingAlpha -> {
                val selectorPosition = alphaRectangle.closestPointTo(position)
                state.alpha = alphaRectangle.valueFrom(selectorPosition)
            }

            else -> {}
        }
    }
    val onInputEnd = {
        currentAction = CurrentAction.None
    }
    Row(modifier = modifier.fillMaxWidth())
    {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onInputStart(it); },
                        onPress = { onInputStart(it); }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = onInputStart,
                        onDragEnd = onInputEnd,
                        onDragCancel = onInputEnd,
                        onDrag = onInputUpdate
                    )
                }
        ) {
            val strokeWidth = 3.dp.toPx()
            val height = size.height * aspectRatio
            val width = size.width
            canvasSize = size
            hueRing.set(
                center = Vector2F(width * 0.5f, height * 0.5f).toCanvasSpace(canvasSize),
                innerRadius = size.width * 0.38f,
                outerRadius = size.width * 0.48f,
            )
            hueSelectorTriangle.set(
                center = (ComplexF.fromPolar(
                    magnitude = lerp(
                        hueRing.innerRadius, hueRing.outerRadius, 0.6666667f
                    ),
                    phase = AngleF.fromDegrees(state.hsv.hue).radians
                ).toVector2F() + hueRing.center),
                orientation = AngleF.fromDegrees(state.hsv.hue + 90f).toComplexF(),
                sideLength = hueRing.width
            )
            hsvTriangle.set(
                center = Vector2F(width * 0.5f, height * 0.5f).toCanvasSpace(canvasSize),
                orientation = AngleF.fromDegrees(state.hsv.hue - 90f).toComplexF(),
                sideLength = hueRing.innerRadius * sqrt(3f)
            )
            drawHsvTriangle(
                triangle = hsvTriangle,
                hue = state.hsv.hue
            )
            drawHueRing(ring = hueRing)
            drawHueSelector(
                selectorTriangle = hueSelectorTriangle,
                hueRing = hueRing,
                strokeWidth = strokeWidth,
                hue = state.hsv.hue,
            )
            if (supportsAlphaPicking) {
                alphaRectangle.set(
                    center = Vector2F(width * 0.5f, height * 1.05f)
                        .toCanvasSpace(canvasSize),
                    width = width * 0.9f,
                    height = height * 0.075f,
                    cornerRadius = height * 0.075f * 0.499f
                )
                drawAlphaRectangle(
                    rectangle = alphaRectangle,
                    state = state
                )
                drawAlphaCircleSelector(
                    position = alphaRectangle.valuePosition(state.alpha),
                    radius = alphaRectangle.cornerRadius,
                    strokeWidth = strokeWidth,
                    state = state
                )
            }
            drawColorSelector(
                position = hsvTriangle.colorPosition(state.hsv.saturation, state.hsv.value),
                state = state,
                strokeWidth = strokeWidth,
                supportsAlphaPicking = supportsAlphaPicking
            )
        }
    }
}

@Composable
fun HslColorPicker(
    state: ColorPickerState, supportsAlphaPicking: Boolean, modifier: Modifier = Modifier
) {
    var currentAction by remember { mutableStateOf(CurrentAction.None) }
    val hueRing = remember {
        MutableAnnulus(
            center = Vector2F.ZERO,
            orientation = ComplexF.ONE,
            innerRadius = 1f,
            outerRadius = 2f,
        )
    }
    val hueSelectorTriangle = remember {
        MutableRegularTriangle(
            center = Vector2F.ZERO,
            orientation = AngleF.fromDegrees(-90f).toComplexF(),
            sideLength = 100f
        )
    }
    val hslSquare = remember {
        MutableSquare(
            center = Vector2F.ZERO,
            orientation = AngleF.fromDegrees(-90f).toComplexF(),
            sideLength = 100f
        )
    }
    val alphaRectangle = remember {
        MutableRoundedRectangle(
            center = Vector2F.ZERO,
            orientation = ComplexF.ONE,
            width = 1f,
            height = 1f,
            cornerRadius = 0f
        )
    }
    var canvasSize by remember { mutableStateOf(Size(1f, 1f)) }
    val aspectRatio = if (supportsAlphaPicking) 0.9f else 1f
    val onInputStart = { startPosition: Offset ->
        val position = startPosition
            .toVector2F()
            .toCanvasSpace(canvasSize)

        when {
            position in hueRing -> {
                currentAction = CurrentAction.ChangingHue
                state.hsl = state.hsl.copy(hue = hueRing.hueFrom(position))
            }

            position in hslSquare -> {
                currentAction = CurrentAction.ChangingSL
                state.hsl = state.hsl.copy(
                    saturation = hslSquare.colorSaturationFrom(position),
                    lightness = hslSquare.colorLightnessFrom(position)
                )
            }

            supportsAlphaPicking && position in alphaRectangle -> {
                currentAction = CurrentAction.ChangingAlpha
                state.alpha = alphaRectangle.valueFrom(position)
            }
        }
    }
    val onInputUpdate = { change: PointerInputChange, _: Offset ->
        val position = change.position
            .toVector2F()
            .toCanvasSpace(canvasSize)

        when (currentAction) {
            CurrentAction.ChangingHue -> {
                state.hsl = state.hsl.copy(hue = hueRing.hueFrom(position))
            }

            CurrentAction.ChangingSL -> {
                val selectorPosition = hslSquare.closestPointTo(position)
                state.hsl = state.hsl.copy(
                    saturation = hslSquare.colorSaturationFrom(selectorPosition),
                    lightness = hslSquare.colorLightnessFrom(selectorPosition)
                )
            }

            CurrentAction.ChangingAlpha -> {
                val selectorPosition = alphaRectangle.closestPointTo(position)
                state.alpha = alphaRectangle.valueFrom(selectorPosition)
            }

            else -> {}
        }
    }
    val onInputEnd = {
        currentAction = CurrentAction.None
    }
    Row(modifier = modifier.fillMaxWidth())
    {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onInputStart(it); },
                        onPress = { onInputStart(it); }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = onInputStart,
                        onDragEnd = onInputEnd,
                        onDragCancel = onInputEnd,
                        onDrag = onInputUpdate
                    )
                }
        ) {
            val strokeWidth = 3.dp.toPx()
            val height = size.height * aspectRatio
            val width = size.width
            canvasSize = size
            hueRing.set(
                center = Vector2F(width * 0.5f, height * 0.5f).toCanvasSpace(canvasSize),
                innerRadius = width * 0.38f,
                outerRadius = width * 0.48f,
            )
            hueSelectorTriangle.set(
                center = (ComplexF.fromPolar(
                    magnitude = lerp(
                        hueRing.innerRadius, hueRing.outerRadius, 0.6666667f
                    ),
                    phase = AngleF.fromDegrees(state.hsv.hue).radians
                ).toVector2F() + hueRing.center),
                orientation = AngleF.fromDegrees(state.hsv.hue + 90f).toComplexF(),
                sideLength = hueRing.width
            )
            hslSquare.set(
                center = Vector2F(width * 0.5f, height * 0.5f).toCanvasSpace(canvasSize),
                orientation = AngleF.fromDegrees(state.hsl.hue - 90f).toComplexF(),
                sideLength = hueRing.innerRadius * sqrt(2f)
            )
            drawHslSquare(
                square = hslSquare,
                hue = state.hsl.hue
            )
            drawHueRing(ring = hueRing)
            drawHueSelector(
                selectorTriangle = hueSelectorTriangle,
                hueRing = hueRing,
                strokeWidth = strokeWidth,
                hue = state.hsl.hue
            )
            if (supportsAlphaPicking) {
                alphaRectangle.set(
                    center = Vector2F(width * 0.5f, height * 1.05f)
                        .toCanvasSpace(canvasSize),
                    width = width * 0.9f,
                    height = height * 0.075f,
                    cornerRadius = height * 0.075f * 0.499f
                )
                drawAlphaRectangle(
                    rectangle = alphaRectangle,
                    state = state
                )
                drawAlphaCircleSelector(
                    position = alphaRectangle.valuePosition(state.alpha),
                    radius = alphaRectangle.cornerRadius,
                    strokeWidth = strokeWidth,
                    state = state
                )
            }
            drawColorSelector(
                position = hslSquare.colorPosition(state.hsl.saturation, state.hsl.lightness),
                strokeWidth = strokeWidth,
                state = state,
                supportsAlphaPicking = supportsAlphaPicking
            )
        }
    }
}

@Composable
fun RgbColorPicker(
    state: ColorPickerState, supportsAlphaPicking: Boolean, modifier: Modifier = Modifier
) {
    var currentAction by remember { mutableStateOf(CurrentAction.None) }
    val rgbCircle = remember {
        MutableCircle(
            center = Vector2F.ZERO,
            orientation = ComplexF.ONE,
            radius = 1f,
        )
    }
    val valueRectangle = remember {
        MutableRoundedRectangle(
            center = Vector2F.ZERO,
            orientation = ComplexF.ONE,
            width = 1f,
            height = 1f,
            cornerRadius = 0f
        )
    }
    val alphaRectangle = remember {
        MutableRoundedRectangle(
            center = Vector2F.ZERO,
            orientation = ComplexF.ONE,
            width = 1f,
            height = 1f,
            cornerRadius = 0f
        )
    }
    var canvasSize by remember { mutableStateOf(Size(1f, 1f)) }
    val aspectRatio = if (supportsAlphaPicking) 0.9f else 1f
    val onInputStart = { startPosition: Offset ->
        val position = startPosition
            .toVector2F()
            .toCanvasSpace(canvasSize)

        when {
            position in rgbCircle -> {
                currentAction = CurrentAction.ChangingColor
                state.hsv = state.hsv.copy(
                    hue = rgbCircle.hueFrom(position),
                    saturation = rgbCircle.saturationFrom(position)
                )
            }

            position in valueRectangle -> {
                currentAction = CurrentAction.ChangingValue
                state.hsv = state.hsv.copy(value = valueRectangle.valueFrom(position))
            }

            supportsAlphaPicking && position in alphaRectangle -> {
                currentAction = CurrentAction.ChangingAlpha
                state.alpha = alphaRectangle.valueFrom(position)
            }
        }
    }
    val onInputUpdate = { change: PointerInputChange, _: Offset ->
        val position = change.position
            .toVector2F()
            .toCanvasSpace(canvasSize)

        when (currentAction) {
            CurrentAction.ChangingColor -> {
                val selectorPosition = rgbCircle.closestPointTo(position)
                state.hsv = state.hsv.copy(
                    hue = rgbCircle.hueFrom(selectorPosition),
                    saturation = rgbCircle.saturationFrom(selectorPosition)
                )
            }

            CurrentAction.ChangingValue -> {
                val selectorPosition = valueRectangle.closestPointTo(position)
                state.hsv = state.hsv.copy(value = valueRectangle.valueFrom(selectorPosition))
            }

            CurrentAction.ChangingAlpha -> {
                val selectorPosition = alphaRectangle.closestPointTo(position)
                state.alpha = alphaRectangle.valueFrom(selectorPosition)
            }

            else -> {}
        }
    }
    val onInputEnd = {
        currentAction = CurrentAction.None
    }
    Row(modifier = modifier.fillMaxWidth())
    {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onInputStart(it); },
                        onPress = { onInputStart(it); }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = onInputStart,
                        onDragEnd = onInputEnd,
                        onDragCancel = onInputEnd,
                        onDrag = onInputUpdate
                    )
                }
        ) {
            val strokeWidth = 3.dp.toPx()
            val height = size.height * aspectRatio
            val width = size.width
            canvasSize = size
            rgbCircle.set(
                center = Vector2F(width * 0.5f, height * 0.45f).toCanvasSpace(canvasSize),
                radius = width * 0.43f,
            )
            valueRectangle.set(
                center = Vector2F(width * 0.5f, height * 0.95f).toCanvasSpace(canvasSize),
                width = width * 0.9f,
                height = height * 0.075f,
                cornerRadius = height * 0.075f * 0.499f
            )
            drawRgbCircle(circle = rgbCircle)
            drawColorValueRectangle(
                rectangle = valueRectangle,
                state = state
            )
            drawColorCircleSelector(
                position = valueRectangle.valuePosition(state.hsv.value),
                radius = valueRectangle.cornerRadius,
                strokeWidth = strokeWidth,
                state = state
            )
            if (supportsAlphaPicking) {
                alphaRectangle.set(
                    center = Vector2F(width * 0.5f, height * 1.05f)
                        .toCanvasSpace(canvasSize),
                    width = width * 0.9f,
                    height = height * 0.075f,
                    cornerRadius = height * 0.075f * 0.499f
                )
                drawAlphaRectangle(
                    rectangle = alphaRectangle,
                    state = state
                )
                drawAlphaCircleSelector(
                    position = alphaRectangle.valuePosition(state.alpha),
                    radius = alphaRectangle.cornerRadius,
                    strokeWidth = strokeWidth,
                    state = state
                )
            }
            drawColorSelector(
                position = rgbCircle.colorPosition(state.hsv.hue, state.hsv.saturation),
                strokeWidth = strokeWidth,
                state = state,
                supportsAlphaPicking = supportsAlphaPicking
            )
        }
    }
}