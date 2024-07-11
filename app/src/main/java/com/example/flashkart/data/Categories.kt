package com.example.flashkart.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Categories(
    @StringRes val stringResourceId:Int,
    @DrawableRes val imageResourceId:Int
){

}
