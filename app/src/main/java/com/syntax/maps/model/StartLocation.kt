package com.syntax.maps.model

import com.google.gson.annotations.SerializedName

class StartLocation {

    @SerializedName("lng")
    var lng: Double = 0.toDouble()

    @SerializedName("lat")
    var lat: Double = 0.toDouble()
}