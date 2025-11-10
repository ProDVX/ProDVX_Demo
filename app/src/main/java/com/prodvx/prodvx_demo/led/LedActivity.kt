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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.prodvx.prodvx_demo.LRGB
import com.prodvx.prodvx_demo.api.sendRequest
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LedActivity : ComponentActivity() {

    fun <T> Iterable<T>.times(count: Int) = (1..count).flatMap { this }
    private val halfBlueHalfRed = listOf("0x010000FF").times(18) + listOf("0x01FF0000").times(27) + listOf("0x010000FF").times(9)

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
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras 'color', 0x01FF0000"
                            }) { Text("SDK: Set Leds Red")},
                        Button (
                            modifier = mod,
                            onClick = {
                                changeLedColorSdk(applicationContext, 0, 0x0100FF00)
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras 'color', 0x0100FF00"
                            }) { Text("SDK: Set Leds Green") },
                        Button(
                            modifier = mod,
                            onClick = {
                                changeLedColorSdk(applicationContext, 0, 0x010000FF)
                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras 'color', 0x0100FF00"
                            }) { Text("SDK: Set Leds Blue") },
//                        Button(
//                            modifier = mod,
//                            onClick = {
//                                changeLedColorSdk(applicationContext, 0, 0x010000FF)
//                                text = "Using SDK: Intent with action.CHANGE_LED_COLOR, and Extras 'color', 0x0100FF00"
//                            }) { Text("SDK: Set Leds Half Blue Half Red") },

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

fun changeLedColorSdk(context: Context, colordemo: Int, color: Int?) {
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

    context.sendBroadcast(intent)
}

fun changeLedColorApi(lrgb: LRGB) {
    println("ChangeLedColorApi with color: ${lrgb},")
    CoroutineScope(Dispatchers.IO).launch {
        sendRequest(HttpMethod.Get, "/setAllLeds", mapOf("lrgb" to "${lrgb.L},${lrgb.R},${lrgb.G},${lrgb.B}"))
    }
}