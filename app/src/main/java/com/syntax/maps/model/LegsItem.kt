package com.syntax.maps.model

import com.google.gson.annotations.SerializedName

class LegsItem {

    @SerializedName("duration")
    var duration: Duration? = null

    @SerializedName("start_location")
    var startLocation: StartLocation? = null

    @SerializedName("distance")
    var distance: Distance? = null

    @SerializedName("start_address")
    var startAddress: String? = null

    @SerializedName("end_location")
    var endLocation: EndLocation? = null

    @SerializedName("end_address")
    var endAddress: String? = null

    @SerializedName("via_waypoint")
    var viaWaypoint: List<Any>? = null

    @SerializedName("steps")
    var steps: List<StepsItem>? = null

    @SerializedName("traffic_speed_entry")
    var trafficSpeedEntry: List<Any>? = null
}