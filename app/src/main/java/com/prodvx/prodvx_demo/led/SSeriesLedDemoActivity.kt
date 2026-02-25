package com.prodvx.prodvx_demo.led

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prodvx.prodvx_demo.R
import com.prodvx.prodvx_demo.api.sendRequest
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SSeriesLedDemoActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidTestTheme {
                Surface(modifier = Modifier.fillMaxSize()){
                    LedDemoApp(this)
                }
            }
        }
    }
}

fun <T> Iterable<T>.times(count: Int) = (1..count).flatMap { this }

val rainbow: List<String> = listOf(
    "0xFFFF0000",
    "0xFFFF3000",
    "0xFFFF6000",
    "0xFFFF9000",
    "0xFFFFC000",
    "0xFFFFF000",
    "0xFFC0FF00",
    "0xFF90FF00",
    "0xFF60FF00",
    "0xFF30FF00",
    "0xFF00FF00",
    "0xFF00FF30",
    "0xFF00FF60",
    "0xFF00FF90",
    "0xFF00FFC0",
    "0xFF00FFF0",
    "0xFF00C0FF",
    "0xFF0090FF",
    "0xFF0060FF",
    "0xFF0030FF",
    "0xFF0000FF",
    "0xFF3000FF",
    "0xFF6000FF",
    "0xFF9000FF",
    "0xFFC000FF",
    "0xFFF000FF",
    "0xFFFF00C0",
    "0xFFFF0090",
    "0xFFFF0060",
    "0xFFFF0030",
    "0xFFFF0000",
    "0xFFFF3000",
    "0xFFFF6000",
    "0xFFFF9000",
    "0xFFFFC000",
    "0xFFFFF000",
    "0xFFC0FF00",
    "0xFF90FF00",
    "0xFF60FF00",
    "0xFF30FF00",
    "0xFF00FF00",
    "0xFF00FF30",
    "0xFF00FF60",
    "0xFF00FF90",
    "0xFF00FFC0",
    "0xFF00FFF0",
    "0xFF00C0FF",
    "0xFF0090FF",
    "0xFF0060FF",
    "0xFF0030FF",
    "0xFF0000FF",
    "0xFF3000FF",
    "0xFF6000FF",
    "0xFF9000FF"
)
val borderRainbow = listOf(
    Color(0xFFff0000),    // Red
    Color(0xFFff6900),    // Red-Orange
    Color(0xFFffa500),    // Orange
    Color(0xFFffff00),    // Yellow
    Color(0xFFadff2f),    // Yellow-Green
    Color(0xFF00ff00),    // Green
    Color(0xFF00ced1),    // Turquoise
    Color(0xFF00ffff),    // Cyan
    Color(0xFF0000ff),    // Blue
    Color(0xFF4b0082),    // Indigo
    Color(0xFF9400d3),     // Violet
    Color(0xFFff0000),    // Red
)
val gradient: List<String> = listOf(
    "0xFFFF0000",
    "0xFFFC0003",
    "0xFFF80007",
    "0xFFF5000A",
    "0xFFF1000E",
    "0xFFEE0011",
    "0xFFEA0015",
    "0xFFE70018",
    "0xFFE3001C",
    "0xFFE0001F",
    "0xFFDC0023",
    "0xFFD90026",
    "0xFFD5002A",
    "0xFFD2002D",
    "0xFFCE0031",
    "0xFFCB0034",
    "0xFFC70038",
    "0xFFC3003C",
    "0xFFBF033A",
    "0xFFBC0939",
    "0xFFB80F39",
    "0xFFB61536",
    "0xFFB31B36",
    "0xFFB02935",
    "0xFFAD2133",
    "0xFFA92D32",
    "0xFFA63330",
    "0xFFA3392E",
    "0xFFA4392C",
    "0xFFA63929",
    "0xFFA83926",
    "0xFFAA3923",
    "0xFFAC3921",
    "0xFFAE391E",
    "0xFFB0391B",
    "0xFFB23918",
    "0xFFB43916",
    "0xFFB63913",
    "0xFFB83910",
    "0xFFBA390D",
    "0xFFBC390B",
    "0xFFBE3908",
    "0xFFC03905",
    "0xFFC23902",
    "0xFFC43900",
    "0xFFCA3300",
    "0xFFD02D00",
    "0xFFD62700",
    "0xFFDC2100",
    "0xFFE21B00",
    "0xFFE81500",
    "0xFFEE0F00",
    "0xFFF40900",
    "0xFFFA0300",
)
val borderGradient = listOf(
    Color(0xFFFFFAFA),       // Snow White
    Color(0xFFFFFAFA),
    Color(0xFFFFFAFA),
    Color(0xFFFFF5EE),       // Seashell
    Color(0xFFFFF5EE),
    Color(0xFFFFFAF0),       // Floral White
    Color(0xFFFFFACD),       // Lemon Chiffon
    Color(0xFFFFFACD),
    Color(0xFFFFE4B5),       // Moccasin
    Color(0xFFFFE4B5),
    Color(0xFFFFDAB9),       // Peachpuff
    Color(0xFFFFDAB9),
    Color(0xFFFFC0CB),       // Pink
    Color(0xFFFFB6C1),       // LightPink
    Color(0xFFFFA500),       // Orange
    Color(0xFFFF8C00),       // DarkOrange
    Color(0xFFFF4500),       // OrangeRed
    Color(0xFFFA8072),       // Salmon
    Color(0xFFE57373),       // Light Coral
    Color(0xFFE57373),
    Color(0xFFE53935),       // Red
    Color(0xFFD32F2F),       // Dark Red
    Color(0xFFC62828),       // Indian Red
    Color(0xFFB71C1C),       // Darker Red
    Color(0xFFA70000),
    Color(0xFF990000),
    Color(0xFF8B0000),       // Very Dark Red
    Color(0xFF8B0000),
    Color(0xFF8B0000),
    Color(0xFF8B0000),
    Color(0xFF8B0000),
    Color(0xFF8B0000),
    Color(0xFF8B0000),
    Color(0xFFFFFAFA),

    )

val redPulseBorder = listOf(
    Color.Red,
    Color.Transparent,
    Color.Red,
    Color.Transparent,
    Color.Red,
    Color.Transparent,
    Color.Red,
    Color.Transparent,
    Color.Red,
    Color.Transparent,
    Color.Red,
    Color.Transparent,
    Color.Red,
    Color.Transparent,
    Color.Red,
    Color.Transparent,
    Color.Red,
)
val greenPulseBorder = listOf(
    Color.Green,
    Color.Transparent,
    Color.Green,
    Color.Transparent,
    Color.Green,
    Color.Transparent,
    Color.Green,
    Color.Transparent,
    Color.Green,
    Color.Transparent,
    Color.Green,
    Color.Transparent,
    Color.Green,
    Color.Transparent,
    Color.Green,
    Color.Transparent,
    Color.Green,

    )


val allRed: List<String> = listOf("0xFFFF0000").times(54)
val halfRed: List<String> = listOf("0xFFFF0000").times(27)
val allGreen: List<String> = listOf("0xFF00FF00").times(54)
val halfGreen: List<String> = listOf("0xFF00FF00").times(27)
val allBlue: List<String> = listOf("0xFF0000FF").times(54)
val halfBlue: List<String> = listOf("0xFF0000FF").times(27)
val allOff: List<String> = listOf("0x00000000").times(54)
val halfOff: List<String> = listOf("0x00000000").times(27)

var leftSide: List<String> = halfOff
var rightSide: List<String> = halfOff
fun completeValues(): List<String> {
    var leftSideStart: List<String> = leftSide.take(18)
    var leftSideEnd: List<String> = leftSide.subList(18, 27)
    val complete = (leftSideStart + rightSide + leftSideEnd).toMutableList()
    return complete
}

private const val SET_LEDS = "/setLeds"
private const val SET_ALL_LEDS = "/setAllLeds"
private const val SET_ALL_LEDS_OFF = "/setAllLedsOff"
suspend fun apiCall(endpoint: String, params: Map<String, String>? = null) {
    sendRequest(HttpMethod.Get, endpoint, params)
}
fun rotate(input: List<String>): List<String> {
    if (input.isEmpty()) return emptyList()
    return input.drop(1) + input.first()
}
fun rotateBackwards(input: List<String>): List<String> {
    if (input.isEmpty()) return emptyList()
    return input.takeLast(1) + input.dropLast(1)
}
fun setLedsApiCall(scope: CoroutineScope, endpoint: String, params: Map<String, String>? = null): Job {
    return scope.launch {
        apiCall(endpoint, params)
    }
}
fun setLedsWithValues(values: List<String>, scope: CoroutineScope): Job {
    var input = ""
    // Efficiently join strings
    input = values.joinToString(separator = ";")
    // Check if input is empty to avoid unnecessary API call
    if (input.isEmpty()) {
        Log.w("setLedsWithValues", "Empty values provided, skipping API call.")
    }
    return setLedsApiCall(scope, SET_LEDS, mapOf("lrgb" to input))
}

/**
 * Starts a coroutine that continuously rotates the given list of LED values and sends API updates.
 *
 * @param initialValues The list of color strings to start rotating with.
 * @param scope The CoroutineScope to launch the rotation job in.
 * @param delayMillis The delay between each rotation step in milliseconds.
 * @return The Job associated with the launched rotation coroutine.
 */
fun startLedRotation(
    initialValues: List<String>,
    scope: CoroutineScope,
    delayMillis: MutableState<Long>,
    backwards: MutableState<Boolean> = mutableStateOf<Boolean>(false),
    endingValues: List<String>? = null,
    startPosition: Int? = null,
): Job {
    if (initialValues.isEmpty()) {
        Log.w("startLedRotation", "Cannot start rotation with empty values")
        return Job().apply { complete() }
    }

    return scope.launch(Dispatchers.Default) {
        var currentValues = initialValues.toMutableList()
        var currentPos = startPosition
        Log.d("startLedRotation", "Starting rotation. Delay: $delayMillis ms")
        try {
            while (isActive) {
                apiCall(SET_LEDS, mapOf("lrgb" to currentValues.joinToString(separator = ";")))
                if(endingValues != null && currentPos != null) {
                    currentValues[currentPos] = endingValues[currentPos]
                } else if (backwards.value) {
                    currentValues = rotateBackwards(currentValues) as MutableList<String>
                } else {
                    currentValues = rotate(currentValues) as MutableList<String>
                }
                delay(delayMillis.value)
                leftSide = currentValues.subList(0, 18) + currentValues.subList(45, 54)
                rightSide = currentValues.subList(18, 45)
            }
        } catch (_: CancellationException) {
            Log.d("startLedRotation", "Rotation job was cancelled.")
        } catch (e: Exception) {
            Log.e("startLedRotation", "Error during rotation loop", e)
        } finally {
            Log.d("startLedRotation", "Rotation loop finished or cancelled.")
        }
    }
}

@Composable
fun LedDemoApp(ctx: Context?) {
    val activity = ctx as ComponentActivity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Header(
            modifier = Modifier
                .fillMaxWidth()
        )

        LedButtonsList(
            modifier = Modifier
                .fillMaxSize(),
            activity
        )
    }
}

@Composable
fun LedButtonsList(modifier: Modifier = Modifier, ctx: ComponentActivity) {
    val scope = rememberCoroutineScope()

    val currentSpeed = remember { mutableLongStateOf(100L) }

    val rotatingJob = remember { mutableStateOf<Job?>(null) }

    fun cancelRotation() {
        rotatingJob.value?.let {
            if (it.isActive) {
                it.cancel()
                Log.d("LedButtonsList", "Rotation Job canceled")
            }
        }
        rotatingJob.value = null
    }

    fun beginRotation(values: List<String>, delayMs: MutableState<Long>, endingValues: List<String>? = null, startPosition: Int? = null) {
        cancelRotation()
        rotatingJob.value = startLedRotation(
            initialValues = values,
            scope = scope,
            delayMillis = delayMs,
            endingValues = endingValues,
            startPosition = startPosition
        )
    }

    val actions = listOf(
        Action(
            title = "Full Red",
            call = {
                cancelRotation() // Cancel rotation before setting new state
                leftSide = halfRed
                rightSide = halfRed
                setLedsApiCall(scope, SET_ALL_LEDS, mapOf("lrgb" to "255,255,0,0"))
            },
            border = BorderStroke(3.dp, Color.Red)
        ),
        Action(
            title = "Full Green",
            call = {
                cancelRotation()
                leftSide = halfGreen
                rightSide = halfGreen
                setLedsApiCall(scope, SET_ALL_LEDS, mapOf("lrgb" to "255,0,255,0"))
            },
            border = BorderStroke(3.dp, Color.Green)
        ),
        Action(
            title = "Full Blue",
            call = {
                cancelRotation()
                leftSide = halfBlue
                rightSide = halfBlue
                setLedsApiCall(scope, SET_ALL_LEDS, mapOf("lrgb" to "255,0,0,255"))
            },
            border = BorderStroke(3.dp, Color.Blue)
        ),
        Action(
            title = "Left Red",
            call = {
                cancelRotation()
                leftSide = halfRed
                setLedsWithValues(completeValues(), scope)
            },
            border = BorderStroke(
                3.dp, Brush.horizontalGradient(
                    0.0f to Color.Red,
                    0.45f to Color.Red,
                    0.55f to Color.Transparent,
                    1f to Color.Transparent,
                    startX = 0.0f,
                    endX = Float.POSITIVE_INFINITY
                )
            )
        ),
        Action(
            title = "Gradient",
            call = {
                cancelRotation()
                leftSide = gradient.subList(0, 18) + gradient.subList(45, 54)
                rightSide = gradient.subList(18, 45)
                setLedsWithValues(gradient, scope)
            },
            border = BorderStroke(3.dp, Brush.sweepGradient(colors = borderGradient))
        ),
        Action(
            title = "Right Red",
            call = {
                cancelRotation()
                rightSide = halfRed
                setLedsWithValues(completeValues(), scope)
            },
            border = BorderStroke(
                3.dp, Brush.horizontalGradient(
                    0.0f to Color.Transparent,
                    0.45f to Color.Transparent,
                    0.55f to Color.Red,
                    1f to Color.Red,
                    startX = 0.0f,
                    endX = Float.POSITIVE_INFINITY
                )
            )
        ),
        Action(
            title = "Left Green",
            call = {
                cancelRotation()
                leftSide = halfGreen
                setLedsWithValues(completeValues(), scope)
            },
            border = BorderStroke(
                3.dp, Brush.horizontalGradient(
                    0.0f to Color.Green,
                    0.45f to Color.Green,
                    0.55f to Color.Transparent,
                    1f to Color.Transparent,
                    startX = 0.0f,
                    endX = Float.POSITIVE_INFINITY
                )
            )
        ),
        Action(
            title = "Rainbow",
            call = {
                beginRotation(values = rainbow, delayMs = currentSpeed)
            },
            border = BorderStroke(3.dp, Brush.sweepGradient(colors = borderRainbow))
        ),
        Action(
            title = "Right Green",
            call = {
                cancelRotation()
                rightSide = halfGreen
                setLedsWithValues(completeValues(), scope)
            },
            border = BorderStroke(
                3.dp, Brush.horizontalGradient(
                    0.0f to Color.Transparent,
                    0.45f to Color.Transparent,
                    0.55f to Color.Green,
                    1f to Color.Green,
                    startX = 0.0f,
                    endX = Float.POSITIVE_INFINITY
                )
            )
        ),
        Action(
            title = "Pulse Red",
            call = {
                val pulsePattern = List(27) { "255,255,0,0" } + List(27) { "0,0,0,0" }
                beginRotation(values = pulsePattern, delayMs = currentSpeed)
            },
            border = BorderStroke(3.dp, Brush.sweepGradient(colors = redPulseBorder))
        ),
        Action(
            title = "Rotating Gradient",
            call = {
                beginRotation(values = gradient, delayMs = currentSpeed)
            },
            border = BorderStroke(3.dp, Brush.sweepGradient(colors = borderGradient.reversed()))
        ),
        Action(
            title = "Pulse Green",
            call = {
                val pulsePattern = List(27) { "255,0,255,0" } + List(27) { "0,0,0,0" }
                beginRotation(values = pulsePattern, delayMs = currentSpeed)
            },
            border = BorderStroke(3.dp, Brush.sweepGradient(colors = greenPulseBorder))
        ),
        Action(
            title = "Left Off",
            call = {
                cancelRotation()
                leftSide = halfOff
                setLedsWithValues(completeValues(), scope)
            },
            border = BorderStroke(
                3.dp, Brush.horizontalGradient(
                    0.0f to Color.Gray,
                    0.45f to Color.Gray,
                    0.55f to Color.Transparent,
                    1f to Color.Transparent,
                    startX = 0.0f,
                    endX = Float.POSITIVE_INFINITY
                )
            )
        ),
        Action(
            title = "All Off",
            call = {
                cancelRotation()
                leftSide = halfOff
                rightSide = halfOff
                setLedsApiCall(scope, SET_ALL_LEDS_OFF)
            },
            border = BorderStroke(3.dp, Color.DarkGray)
        ),
        Action(
            title = "Right Off",
            call = {
                cancelRotation()
                rightSide = halfOff
                setLedsWithValues(completeValues(), scope)
            },
            border = BorderStroke(
                3.dp, brush = Brush.horizontalGradient(
                    0.0f to Color.Transparent,
                    0.45f to Color.Transparent,
                    0.55f to Color.Gray,
                    1f to Color.Gray,
                    startX = 0.0f,
                    endX = Float.POSITIVE_INFINITY
                )
            )
        ),
        Action(
            title = "",
            call = { cancelRotation() },
        ),
        Action(
            title = "Go Back",
            call = {
                cancelRotation()
                ctx.finish()
            },
            border = BorderStroke(3.dp, Color.DarkGray)
        ),
        Action(
            title = "",
            call = { cancelRotation() },
        )
    )

    Row(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            items(actions) { action ->
                LedButton(
                    text = action.title,
                    border = action.border,
                    onClick = action.call,
                    modifier = action.modifier
                )
            }
        }
    }

}

@Composable
fun LedButton(
    text: String, onClick: () -> Unit, border: BorderStroke? = null, modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        border = border,
        colors = ButtonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Gray
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Text(
            text = text
        )
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.prodvx_cmyk_negative_8),
            contentDescription = null,
            alignment = Alignment.Center,
            modifier = Modifier
                .width(250.dp)
                .padding(16.dp)
        )
        Text(
            text = "Led Demo (S-Series)",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(0.dp, 16.dp, 16.dp, 16.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 720)
@Composable
fun MainPreview() {
    AndroidTestTheme {
        LedDemoApp(null)
    }
}