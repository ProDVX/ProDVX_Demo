package com.prodvx.prodvx_demo.test

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme
import java.lang.reflect.Field
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * This code is EXPERIMENTAL
 *
 * No code is to be used in public/production applications.
 * Only to be used for examples.
 */
class TestActivity: ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0
        )

        setContent{
            AndroidTestTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            printAllBuildFields()
                            printAllBuildVersionFields()

                        }
                    ) { Text("Log OS information")}
                    Button(
                        onClick = {
                            printSoftwareSystemFeatures()
                        }
                    ) { Text("Log Software Supported Features")}
                    Button(
                        onClick = {
                            printAvailableSystemFeatures()
                        }
                    ) { Text("Log Actual Available Features")}

                
                }
            }
        }
    }

    fun printSoftwareSystemFeatures() {
        val pm = this.packageManager

        val features = pm.systemAvailableFeatures
        println("Supported Features:")
        features.forEach {
            println("${it.name}")
        }
    }

    fun printAvailableSystemFeatures(){
        val featureMap = HashMap<String, Boolean>()
        featureMap.put("WiFi", this.getSystemService(WifiManager::class.java ) != null)
        featureMap.put("Bluetooth", this.getSystemService(BluetoothManager::class.java).getAdapter() != null)

        println("Supported Features:")
        featureMap.forEach { string, bool ->
            println("$string: $bool")
        }
    }



    fun printAllBuildFields() {
        val fields: Array<Field> = Build::class.java.fields
        for (field in fields) {
            field.isAccessible = true
            try {
                val name = field.name
                val value = field.get(null) // static fields, so null instance
                println("Build.$name: $value")
            } catch (e: Exception) {
                println("Build.${field.name}: ERROR - ${e.message}")
            }
        }
    }

    fun printAllBuildVersionFields() {
        val fields: Array<Field> = Build.VERSION::class.java.fields
        for (field in fields) {
            field.isAccessible = true
            try {
                println("Build.VERSION.${field.name}: ${field.get(null)}")
            } catch (e: Exception) {
                println("Build.VERSION.${field.name}: ERROR - ${e.message}")
            }
        }
    }

    fun getDisplaySizeUsingExample(context: Context) : Unit {
        println("GetDisplaySizeUsingExample")
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()

        val widthPixels = displayMetrics.widthPixels
        println("WidthPixels: ${widthPixels}")
        val heightPixels = displayMetrics.heightPixels
        println("HeightPixels: ${heightPixels}")
        val xdpi = displayMetrics.xdpi
        println("xdpi: ${xdpi}")
        val ydpi = displayMetrics.ydpi
        println("ydpi: ${ydpi}")

        val widthInches = widthPixels / xdpi
        println("WidthInches: ${widthInches}")
        val heightInches = heightPixels / ydpi
        println("HeightInches: ${heightInches}")

        val diagonalInches = sqrt(widthInches.toDouble().pow(2.0) + heightInches.toDouble().pow(2.0))
        println("Diagonal Inches: $diagonalInches")
    }

    fun getDisplaySizeUsingNew(context: Context): Unit {
        println("GetDisplaySizeUsingNew")
        val displayMetrics = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics)

        val widthPixels = displayMetrics.widthPixels
        println("WidthPixels: ${widthPixels}")
        val heightPixels = displayMetrics.heightPixels
        println("HeightPixels: ${heightPixels}")
        val xdpi = displayMetrics.xdpi
        println("xdpi: ${xdpi}")
        val ydpi = displayMetrics.ydpi
        println("ydpi: ${ydpi}")

        val density = displayMetrics.density
        println("Density: ${density}")
        val densityDpi = displayMetrics.densityDpi
        println("DensityDpi: ${densityDpi}")

        val widthInches = widthPixels / xdpi
        println("WidthInches: ${widthInches}")
        val heightInches = heightPixels / ydpi
        println("HeightInches: ${heightInches}")

        val widthInchesUsingDensity = widthPixels / densityDpi
        println("WidthInchesDpi: ${widthInchesUsingDensity}")
        val heightInchesUsingDensity = heightPixels / densityDpi
        println("HeightInchesDpi: ${heightInchesUsingDensity}")

        val diagonalInches = sqrt(widthInches.toDouble().pow(2.0) + heightInches.toDouble().pow(2.0))
        println("Diagonal Inches: $diagonalInches")
        val diagonalInchesDensity = sqrt(widthInchesUsingDensity.toDouble().pow(2.0) + heightInchesUsingDensity.toDouble().pow(2.0))
        println("Diagonal Inches Using Density: $diagonalInchesDensity")
    }
}