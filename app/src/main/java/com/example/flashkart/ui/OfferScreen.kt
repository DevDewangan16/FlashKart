package com.example.flashkart.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flashkart.R
import java.lang.reflect.Modifier

@Composable
fun OfferScreen(){
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = androidx.compose.ui.Modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.splashscreenbanner), contentDescription = "",
            modifier = androidx.compose.ui.Modifier.padding(20.dp))
    }
}