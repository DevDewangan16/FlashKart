package com.example.flashkart.network

import com.example.flashkart.data.InternetItems
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URl="https://training-uploads.internshala.com"

private val retrofit=Retrofit.Builder()
    .addConverterFactory(
        Json.asConverterFactory(
            "application/json".toMediaType()
        )
    )
    .baseUrl(BASE_URl)
    .build()

interface FlashApiService{
    @GET("android/grocery_delivery_app/items.json")
    suspend fun getItems():List<InternetItems>
}

object FlashApi{
    val retrofitService:FlashApiService by lazy{
        retrofit.create(
            FlashApiService::class.java
        )
    }
}
