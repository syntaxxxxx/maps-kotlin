package com.syntax.maps.model

import com.google.gson.annotations.SerializedName

class Distance {

    @SerializedName("text")
    var text: String? = null

    @SerializedName("value")
    var value: Int = 0
}