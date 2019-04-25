package com.syntax.maps.model

import com.google.gson.annotations.SerializedName

class StepsItem {

    @SerializedName("duration")
    var duration: Duration? = null

    @SerializedName("start_location")
    var startLocation: StartLocation? = null

    @SerializedName("distance")
    var distance: Distance? = null

    @SerializedName("travel_mode")
    var travelMode: String? = null

    @SerializedName("html_instructions")
    var htmlInstructions: String? = null

    @SerializedName("end_location")
    var endLocation: EndLocation? = null

    @SerializedName("maneuver")
    var maneuver: String? = null

    @SerializedName("polyline")
    var polyline: Polyline? = null
}