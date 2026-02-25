package com.prodvx.prodvx_demo.adaptive_light

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.prodvx.prodvx_demo.MainActivity
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme
import kotlin.math.roundToInt

class AdaptiveLightActivity : ComponentActivity() {
    var myService by mutableStateOf<MediaProjectionService?>(null)
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MediaProjectionService.MyBinder
            myService = binder.getService()
            isBound = true
            Log.d("MainActivity", "Service connected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            myService = null
            Log.d("MainActivity", "Service disconnected")
        }
    }
    private val mediaProjectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private val screenCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val serviceIntent = Intent(this, MediaProjectionService::class.java).apply {
                putExtra("resultCode", result.resultCode)
                putExtra("data", result.data)
            }
            startForegroundService(serviceIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTestTheme {
                AdaptiveLightScreen(myService, this)
                screenCaptureLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Bind to the service
        Intent(this, MediaProjectionService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    @Composable
    fun AdaptiveLightScreen(myService: MediaProjectionService?, context: Context) {
        var vertical by remember{ mutableIntStateOf(myService?.verticalOffset ?: 0) }
        var localVertical by remember{mutableIntStateOf(vertical + 20)}
        var horizontal by remember{ mutableIntStateOf(myService?.horizontalOffset ?: 0)}

        var hide by remember { mutableStateOf(false) }
        var backgroundUri by remember{ mutableStateOf<Uri?>(null) }

        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                try {
                    backgroundUri = uri
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
        if (backgroundUri != null) {
            AsyncImage(
                model = backgroundUri,
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Checkbox(
                checked = hide,
                onCheckedChange = { hide = it }
            )
            if(!hide) Text("Hide Extras")
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    screenCaptureLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
                }) {
                    Text("Start Adaptive Light")
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (myService != null) {
                    Text(text = "Vertical offset")
                    Slider (
                        modifier = Modifier.width(400.dp),
                        value = myService.verticalOffset.toFloat(),
                        onValueChange = { newValue ->
                            val newVal = (newValue /  5).roundToInt() * 5
                            myService.verticalOffset = newVal
                            vertical = newVal - 40
                            localVertical = newVal - 20 },
                        valueRange = 20f..360f,
                        steps = 40
                    )
//                    if(myService.verticalOffset < 40) Text(text= "Warning: Vertical Offset should be 20 or greater to ignore system bars", color = Color.Red)
                    if(myService.verticalOffset < 40) Text(text= "Warning: Increase vertical offset to ignore system bars", color = Color.Red)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Horizontal offset")
                    Slider (
                        modifier = Modifier.width(400.dp),
                        value = myService.horizontalOffset.toFloat(),
                        onValueChange = { newValue ->
                            val newVal = (newValue / 5).roundToInt() * 5
                            myService.horizontalOffset = newVal
                            horizontal = newVal - 40 },
                        valueRange = 40f..640f,
                        steps = 40
                    )

                    if(!hide) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fuzz values?")
                            Checkbox(
                                checked = myService.fuzz,
                                onCheckedChange = { myService.fuzz = it },

                                )
                        }
                    }
                    if(myService.fuzz && !hide) {
                        Slider(
                            value = myService.fuzzSampleSize.toFloat(),
                            onValueChange = { newValue -> myService.fuzzSampleSize = newValue.toInt() },
                            valueRange = 1f..9f,
                            steps = 10,
                            modifier = Modifier.width(400.dp)
                        )
                        Text("Sample size: ${myService.fuzzSampleSize}")
                    }
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                    ) {
//                        Text("S-Series?")
//                        Checkbox(
//                            checked = myService.sseries,
//                            onCheckedChange = { myService.sseries = it }
//                        )
//                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    if(!hide) {
                        Button(
                            onClick = {
                                imagePickerLauncher.launch("image/*")
                            }
                        ){
                            Text("Set Test Image")
                        }
                        Button(onClick = {
                            stopService(Intent(context, MediaProjectionService::class.java))
                        }) {
                            Text("Stop Adaptive Lights")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            val intent = Intent(
                                this@AdaptiveLightActivity,
                                MainActivity::class.java
                            )
                            context.startActivity(intent)
                        } ) { Text("Go Back")}
                    }
                }
            }

            // Draw the visual lines based on the service's offset values.
            if (myService != null) {
                    // Top horizontal line
                    Spacer(modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .offset(y = vertical.dp)
                        .background(Color.Red)
                    )

                    // Bottom horizontal line
                    Spacer(modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .offset(y = -vertical.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.Red)
                    )

                if(myService.horizontalOffset >= 40){
                    // Left vertical line
                    Spacer(modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .offset(x = horizontal.dp)
                        .background(Color.Red)
                    )

                    // Right vertical line
                    Spacer(modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .offset(x = (-horizontal).dp)
                        .align(Alignment.CenterEnd)
                        .background(Color.Red)
                    )
                }
            }
        }
    }
}