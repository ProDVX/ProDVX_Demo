package com.prodvx.prodvx_demo.api_demo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme

class ApiDemoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidTestTheme {
                ApiDemoScreen(this)

            }
        }
    }
}

@Composable
fun ApiDemoScreen(ctx: Context?){
    val activity = ctx as ComponentActivity
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        ApiButtonList(
            modifier = Modifier
                .fillMaxSize(),
            activity
        )
        ApiResponse()
    }
}

@Composable
fun ApiButtonList(modifier: Modifier = Modifier, ctx: ComponentActivity ){

}

@Composable
fun ApiButton(){}

@Composable
fun ApiResponse(){}