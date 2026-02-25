package com.prodvx.prodvx_demo.led

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android_serialport_api.LedUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prodvx.prodvx_demo.BuildConfig
import com.prodvx.prodvx_demo.LRGB
import com.prodvx.prodvx_demo.api.sendRequest
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LedActivity : ComponentActivity() {

    fun <T> Iterable<T>.times(count: Int) = (1..count).flatMap { this }
//    private val halfBlueHalfRed = listOf("0x010000FF").times(18) + listOf("0x01FF0000").times(27) + listOf("0x010000FF").times(9)
    private val halfBlueHalfRed52 = listOf(0x7F0000FF).times(21) + listOf(0x7FFF0000).times(26) + listOf(0x7F0000FF).times( 5)
    private val all52Colors = listOf(0x7FFF0000, 0x7FEF0010, 0x7FDF0020, 0x7FCF0030, 0x7FBF0040, 0x7FAF0050, 0x7F9F0060, 0x7F8F0070, 0x7F7F0080, 0x7F6F0090, 0x7F5F00A0, 0x7F4F00B0, 0x7F3F00C0, 0x7F2F00D0, 0x7F1F00E0, 0x7F0F00F0, 0x7F0000FF, 0x7F0010EF, 0x7F0020DF, 0x7F0030CF, 0x7F0040BF, 0x7F0050AF, 0x7F00609F, 0x7F00708F, 0x7F00807F, 0x7F00906F, 0x7F00A05F, 0x7F00B04F, 0x7F00C03F, 0x7F00D02F, 0x7F00E01F, 0x7F00F00F, 0x7F00FF00, 0x7F10EF00, 0x7F20DF00, 0x7F30CF00, 0x7F40BF00, 0x7F50AF00, 0x7F609F00, 0x7F708F00, 0x7F807F00, 0x7F906F00, 0x7FA05F00, 0x7FB04F00, 0x7FC03F00, 0x7FD02F00, 0x7FE01F00, 0x7FF00F00, 0x7FFF0000, 0x7FEF0010, 0x7FDF0020, 0x7FCF0030)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            AndroidTestTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    var text by remember { mutableStateOf("") }
                    val mod = Modifier.fillMaxWidth()
                    val ledUtils: LedUtils = LedUtils()

                    val buttonsSdk = listOf(
                        Button (
                            modifier = mod,
                            onClick = {
                                changeLedColorSdk(applicationContext, 0, 0x01FF0000)
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras: 'color', 0x01FF0000"
                            }) { Text("SDK (Type C): Set Leds Red")},
                        Button (
                            modifier = mod,
                            onClick = {
                                changeLedColorSdk(applicationContext, 0, 0x0100FF00)
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras: 'color', 0x0100FF00"
                            }) { Text("SDK (Type C): Set Leds Green") },
                        Button(
                            modifier = mod,
                            onClick = {
                                changeLedColorSdk(applicationContext, 0, 0x010000FF)
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras: 'color', 0x0100FF00"
                            }) { Text("SDK (Type C): Set Leds Blue") },
                        
                        Spacer(modifier = Modifier.height(16.dp)),
                        
                        Button(
                            modifier = mod,
                            onClick = {
                                changeLedColorSdk(applicationContext, 1 )
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras: 'colordemo 1'"
                            }) { Text("SDK (Type B): Set Leds to Colordemo 1") },
                        Button(
                            modifier = mod,
                            onClick = {
                                changeLedColorSdk(applicationContext, 0, 0x7FFF0000)
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras: 'color' 0x7FFF0000"
                            }) { Text("SDK (Type B): Set Leds Red") },
                        Button(
                            modifier = mod,
                            onClick = {
                                val colors = all52Colors
                                changeLedColorSdk(applicationContext, 0, null, colors)
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras: 'colors', 52 colors"
                            }) { Text("SDK (Type B): Set Leds to 52 Colors") },
                    )


                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .weight(1f)
                                .width(50.dp)
                        ){ items(buttonsSdk) {} }
                    }

                    // PogoLED Buttons
                    if (BuildConfig.IS_DEVELOPMENT) {
                        // PLED Buttons
                        val buttonsPogo = listOf(
                            Button(
                                modifier = mod,
                                onClick = {
                                    ledUtils.ledsController("FF0000")
                                }) { Text("PogoLed: Set to Red")},
                            Button(
                                modifier = mod,
                                onClick = {
                                    ledUtils.ledsController("00FF00")
                                }) { Text("PogoLed: Set to Green")},
                            Button(
                                modifier = mod,
                                onClick = {
                                    ledUtils.ledsController("0000FF")
                                }) { Text("PogoLed: Set to Blue")},
                        )

                        // PLed Button placing
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .weight(1f)
                                    .width(50.dp)
                            ){ items(buttonsPogo) {} }
                        }
                    }

                    Button (
                        onClick = {
                            changeLedColorApi(LRGB(255, 0, 0, 255))
                            text = "Using API: URL = http://localhost:3535/setAllLeds?lrgb=255,0,0,255"
                        }
                    ) {
                        Text("Set Leds Blue using API")
                    }
                    Text(text)
                    Button(onClick = {
                        val intent = Intent(this@LedActivity, SSeriesLedDemoActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text("S-Series Led Demo")
                    }
                    Button(
                        onClick= {
                            this@LedActivity.finish()
                        }
                    ) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}

fun changeLedColorSdk(context: Context, colordemo: Int, color: Int? = null, colors: List<Int>? = null) {
    println("ChangeLedColorSdk with colordemo: ${colordemo} and color: $color")
    val intent = Intent("action.CHANGE_LED_COLOR")

    if(colordemo < 0 || colordemo > 5) {
        return
    }

    if(colordemo > 0) {
        intent.putExtra("colordemo", colordemo)
    }

    if(color != null && color > 0) {
        intent.putExtra("color", color)
    }
    
    if(color==null && colors != null) {
        intent.putExtra("colors", colors.toIntArray())
    }

    println("Intent: $intent")
    context.sendBroadcast(intent)
}

fun changeLedColorApi(lrgb: LRGB) {
    println("ChangeLedColorApi with color: ${lrgb},")
    CoroutineScope(Dispatchers.IO).launch {
        sendRequest(HttpMethod.Get, "/setAllLeds", mapOf("lrgb" to "${lrgb.L},${lrgb.R},${lrgb.G},${lrgb.B}"))
    }
}