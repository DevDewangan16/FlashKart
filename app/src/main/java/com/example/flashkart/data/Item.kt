package com.example.flashkart.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

data class Item(
    @StringRes val stringResourceId:Int,
    @StringRes val itemCategoryId:Int,
    val itemQuantityId:String,
    val itemPrice:Int,
    @DrawableRes val imageResourceId: @Composable Int

)
