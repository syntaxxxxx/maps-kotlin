package com.syntax.maps.model

import com.google.gson.annotations.SerializedName

class GeocodedWaypointsItem {

    @SerializedName("types")
    var types: List<String>? = null

    @SerializedName("geocoder_status")
    var geocoderStatus: String? = null

    @SerializedName("place_id")
    var placeId: String? = null
}