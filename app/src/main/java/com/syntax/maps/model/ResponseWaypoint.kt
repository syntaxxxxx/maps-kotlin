package com.syntax.maps.model

import com.google.gson.annotations.SerializedName

class ResponseWaypoint {

    @SerializedName("routes")
    var routes: List<RoutesItem>? = null

    @SerializedName("geocoded_waypoints")
    var geocodedWaypoints: List<GeocodedWaypointsItem>? = null

    @SerializedName("status")
    var status: String? = null
}