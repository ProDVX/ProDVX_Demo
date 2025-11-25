package com.prodvx.prodvx_demo.test

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This code is EXPERIMENTAL
 *
 * No code is to be used in public/production applications.
 * Only to be used for examples.
 */
class TestActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            AndroidTestTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { getDisplaySizeUsingExample(applicationContext) }
                    ) {
                        Text("Get Size using example")
                    }

                    Button(
                        onClick = { getDisplaySizeUsingNew(applicationContext) }
                    ) {
                        Text("Get Display props new")
                    }
                }
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