package com.hotelka.knitlyWants.ui.theme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kotlin.also
import kotlin.apply
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

enum class WaveDirection { RIGHT, LEFT }
@Composable
fun WaveProgress(
    modifier: Modifier = Modifier.background(Transparent),
    progress: Float,
    fillBrush: Brush? = null,
    color: Color? = headers_activeElement,
    amplitudeRange: ClosedFloatingPointRange<Float> = 30f..50f,
    waveSteps: Int = 20,
    waveFrequency: Int = 3,
    phaseShiftDuration: Int = 2000,
    amplitudeDuration: Int = 2000,
    waveDirection: WaveDirection = WaveDirection.RIGHT
) {
    val path = remember { Path() } //reusing same path object to reduce object creation and gc calls
    val coroutineScope = rememberCoroutineScope()
    val phaseShift = remember { Animatable(0f) }
    val amplitude = remember { Animatable(amplitudeRange.start) }

    LaunchedEffect(amplitudeRange, amplitudeDuration) {
        coroutineScope.launch {
            amplitude.stop()
            amplitude.snapTo(amplitudeRange.start)
            amplitude.animateTo(
                targetValue = amplitudeRange.endInclusive,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = amplitudeDuration, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    LaunchedEffect(phaseShiftDuration) {
        coroutineScope.launch {
            phaseShift.stop()
            phaseShift.snapTo(0f)
            phaseShift.animateTo(
                targetValue = (2 * PI).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = phaseShiftDuration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Box(
        modifier = modifier
            .drawBehind {
                val yPos = (1 - progress) * size.height

                path
                    .apply {
                        reset()
                        val phaseShiftLocal = when (waveDirection) {
                            WaveDirection.RIGHT -> -phaseShift.value
                            WaveDirection.LEFT -> phaseShift.value
                        }
                        prepareSinePath(this, size, waveFrequency, amplitude.value, phaseShiftLocal, yPos, waveSteps)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    .also {
                        if (fillBrush != null) {
                            drawPath(path = it, brush = fillBrush, style = Fill)
                        } else if (color != null) {
                            drawPath(path = it, color = color, style = Fill)
                        } else {
                            throw IllegalArgumentException()
                        }
                    }
            }
    )
}

@Preview
@Composable
fun WaveProgressPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        WaveProgress(progress = 0.5f, modifier = Modifier.fillMaxSize())
    }
}

fun prepareSinePath(
    path: Path,
    size: Size,
    frequency: Int,
    amplitude: Float,
    phaseShift: Float,
    position: Float,
    step: Int
) {
    for (x in 0..size.width.toInt().plus(step) step step) {
        val y = position + amplitude * sin(x * frequency * Math.PI / size.width + phaseShift).toFloat()
        if (path.isEmpty)
            path.moveTo(x.toFloat(), max(0f, min(y, size.height.toFloat())))
        else
            path.lineTo(x.toFloat(), max(0f, min(y, size.height.toFloat())))
    }
}