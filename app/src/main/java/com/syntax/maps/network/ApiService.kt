package com.syntax.maps.network


import com.syntax.maps.model.ResponseWaypoint

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/directions/json")
    fun request_route(
            @Query("origin") origin: String,
            @Query("destination") tujuan: String,
            @Query("key") key: String): Call<ResponseWaypoint>
}
