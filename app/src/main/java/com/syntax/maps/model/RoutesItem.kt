package com.syntax.maps.model

import com.google.gson.annotations.SerializedName

class RoutesItem {

    @SerializedName("summary")
    var summary: String? = null

    @SerializedName("copyrights")
    var copyrights: String? = null

    @SerializedName("legs")
    var legs: List<LegsItem>? = null

    @SerializedName("warnings")
    var warnings: List<Any>? = null

    @SerializedName("bounds")
    var bounds: Bounds? = null

    @SerializedName("overview_polyline")
    var overviewPolyline: OverviewPolyline? = null

    @SerializedName("waypoint_order")
    var waypointOrder: List<Any>? = null
}