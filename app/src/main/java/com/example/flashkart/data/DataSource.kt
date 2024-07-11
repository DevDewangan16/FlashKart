package com.example.flashkart.data

import androidx.annotation.StringRes
import com.example.flashkart.R

object DataSource {
     fun loadCategories():List<Categories> {
         return listOf(
             Categories(R.string.fruits,R.drawable.fruits),
             Categories(R.string. vegetables,R.drawable.vegetables),
             Categories(R.string.beverages,R.drawable.beverages),
             Categories(R.string.stationery,R.drawable.stationery),
             Categories(R.string.packaged_food,R.drawable.packaged_food),
             Categories(R.string.munchies,R.drawable.munchies),
             Categories(R.string.kitchen_essentials,R.drawable.kitchen_essential),
             Categories(R.string.cleaning_essentials,R.drawable.cleaning),
             Categories(R.string.bread_biscuits,R.drawable.bread_biscuit),
             Categories(R.string.bath_body,R.drawable.bath_body),
             )
     }}

   /* fun loadItems(
        @StringRes CategoryName:Int
    ):List<Item>{
        return listOf(
            Item(R.string.banana_robusta,R.string.fruits,"1 Kg",1000,R.drawable.banana),
            Item(R.string.shimla_apple,R.string.fruits,"1 Kg",1000,R.drawable.apple),
            Item(R.string.Papaya,R.string.fruits,"1 Kg",1000,R.drawable.papaya),
            Item(R.string.pomegranate,R.string.fruits,"1 Kg",1000,R.drawable.pomegranate),
            Item(R.string.pineapple,R.string.fruits,"1 Kg",1000,R.drawable.pineapple),
            Item(R.string.pepsi,R.string.beverages,"1 Kg",1000,R.drawable.pepsi_can)
        ).filter {
            it.itemCategoryId==CategoryName
        }
    }
}*/