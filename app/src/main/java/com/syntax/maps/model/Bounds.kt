package com.syntax.maps.model

import com.google.gson.annotations.SerializedName

class Bounds {

    @SerializedName("southwest")
    var southwest: Southwest? = null

    @SerializedName("northeast")
    var northeast: Northeast? = null
}