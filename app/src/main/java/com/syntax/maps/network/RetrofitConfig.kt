package com.syntax.maps.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConfig {

    val retrofit: Retrofit
        get() = Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/")
                .addConverterFactory(GsonConverterFactory.create()).build()

    val instanceRetrofit: ApiService
        get() = RetrofitConfig.retrofit.create(ApiService::class.java)

}
