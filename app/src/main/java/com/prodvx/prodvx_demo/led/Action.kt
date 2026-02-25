package com.prodvx.prodvx_demo.led

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class Action(
    val title: String,
    val border: BorderStroke? = null,
    val call: () -> Unit,
    val backgroundColor : Color? = null,
    val modifier: Modifier = Modifier
)
